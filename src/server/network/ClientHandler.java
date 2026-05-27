package server.network;

import server.persistence.*;
import server.service.*;
import shared.domain.*;
import shared.protocol.Request;
import shared.protocol.Response;

import java.io.*;
import java.net.Socket;
import java.util.List;

// Håndterer én klient - kører i sin egen tråd
public class ClientHandler implements Runnable {

    private final Socket socket;

    // Services bruges til at udføre forretningslogikken
    private final AuthService       authService;
    private final VehicleService    vehicleService;
    private final EmployeeService   employeeService;
    private final DepartmentService departmentService;
    private final AssignmentService assignmentService;

    public ClientHandler(Socket socket) {
        this.socket = socket;

        // Opretter DAOs og services
        SqlUserDao       userDao       = new SqlUserDao();
        SqlVehicleDao    vehicleDao    = new SqlVehicleDao();
        SqlEmployeeDao   employeeDao   = new SqlEmployeeDao();
        SqlDepartmentDao departmentDao = new SqlDepartmentDao();
        SqlAssignmentDao assignmentDao = new SqlAssignmentDao();

        this.authService       = new AuthService(userDao);
        this.vehicleService    = new VehicleService(vehicleDao);
        this.employeeService   = new EmployeeService(employeeDao);
        this.departmentService = new DepartmentService(departmentDao);
        this.assignmentService = new AssignmentService(assignmentDao);
    }

    @Override
    public void run() {
        System.out.println("Klient forbundet: " + socket.getRemoteSocketAddress());
        try (ObjectInputStream in  = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            // Bliver ved med at modtage requests fra klienten
            while (true) {
                Request request = (Request) in.readObject();
                Response response = handleRequest(request);
                out.writeObject(response);
                out.flush();
            }

        } catch (EOFException e) {
            System.out.println("Klient afsluttet: " + socket.getRemoteSocketAddress());
        } catch (Exception e) {
            System.out.println("Fejl med klient: " + e.getMessage());
        }
    }

    // Modtager en request og kalder den rigtige service
    private Response handleRequest(Request request) {
        try {
            return switch (request.getType()) {
                case LOGIN -> {
                    String[] creds = (String[]) request.getData();
                    User user = authService.login(creds[0], creds[1]);
                    if (user == null) yield Response.error("Forkert brugernavn eller password");
                    yield Response.ok(user);
                }
                case GET_ALL_VEHICLES -> {
                    List<Vehicle> vehicles = vehicleService.getAll();
                    yield Response.ok(vehicles);
                }
                case CREATE_VEHICLE -> {
                    Vehicle created = vehicleService.create((Vehicle) request.getData());
                    yield Response.ok(created);
                }
                case UPDATE_VEHICLE -> {
                    Vehicle updated = vehicleService.update((Vehicle) request.getData());
                    yield Response.ok(updated);
                }
                case SEARCH_VEHICLES -> {
                    List<Vehicle> result = vehicleService.search((String) request.getData());
                    yield Response.ok(result);
                }
                case GET_ALL_EMPLOYEES -> {
                    List<Employee> employees = employeeService.getAll();
                    yield Response.ok(employees);
                }
                case CREATE_EMPLOYEE -> {
                    Employee created = employeeService.create((Employee) request.getData());
                    yield Response.ok(created);
                }
                case UPDATE_EMPLOYEE -> {
                    Employee updated = employeeService.update((Employee) request.getData());
                    yield Response.ok(updated);
                }
                case GET_ALL_DEPARTMENTS -> {
                    List<Department> departments = departmentService.getAll();
                    yield Response.ok(departments);
                }
                case ASSIGN_VEHICLE -> {
                    int[] ids = (int[]) request.getData();
                    VehicleAssignment assignment = assignmentService.assign(ids[0], ids[1]);
                    yield Response.ok(assignment);
                }
                case UNASSIGN_VEHICLE -> {
                    int vehicleId = (int) request.getData();
                    assignmentService.unassign(vehicleId);
                    yield Response.ok(null);
                }
                case GET_ASSIGNMENT_FOR_VEHICLE -> {
                    int vehicleId = (int) request.getData();
                    VehicleAssignment assignment = assignmentService.getActiveForVehicle(vehicleId);
                    yield Response.ok(assignment);
                }
                default -> Response.error("Ukendt request type");
            };
        } catch (Exception e) {
            System.out.println("Fejl ved håndtering af request: " + e.getMessage());
            return Response.error(e.getMessage());
        }
    }
}
