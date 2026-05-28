# Klassediagram – Fleet Manager arkitektur

Kopiér koden nedenfor ind på https://mermaid.live

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#3B5998', 'primaryTextColor': '#fff', 'primaryBorderColor': '#2C4170', 'lineColor': '#555', 'secondaryColor': '#E8EEF7', 'tertiaryColor': '#F5F7FB'}}}%%
classDiagram
    direction TB

    %% ── KLIENT ──────────────────────────────────
    class LoginController {
        +initialize()
        +onLoginClick()
    }
    class LoginViewModel {
        -username : StringProperty
        -password : StringProperty
        -errorMessage : StringProperty
        +login() boolean
    }
    class VehicleListController {
        +initialize()
        +onSearch()
        +onNewVehicle()
    }
    class VehicleListViewModel {
        -vehicles : ObservableList
        -searchText : StringProperty
        -statusMessage : StringProperty
        +loadVehicles()
        +search()
        +saveVehicle(v)
        +propertyChange(event)
    }
    class EmployeeListViewModel {
        -employees : ObservableList
        -searchText : StringProperty
        +loadEmployees()
        +search()
        +saveEmployee(e)
        +propertyChange(event)
    }
    class ServerConnection {
        -support : PropertyChangeSupport
        -lastResponse : Response
        +connect()
        +send(Request) Response
        +addListener(eventName, PropertyChangeListener)
    }
    class AppContext {
        -connection : ServerConnection
        -currentUser : User
        +getConnection() ServerConnection
        +getCurrentUser() User
    }

    %% ── SHARED ──────────────────────────────────
    class Request {
        -type : RequestType
        -data : Object
        +getType() RequestType
        +getData() Object
    }
    class Response {
        -success : boolean
        -push : boolean
        -data : Object
        -message : String
        +ok(data) Response
        +error(msg) Response
        +push(event) Response
        +isPush() boolean
    }

    %% ── SERVER ──────────────────────────────────
    class Server {
        -threadPool : ExecutorService
        -connectedClients : HashSet
        +start()
        +unregister(handler)
        +broadcast(push, sender)
    }
    class ClientHandler {
        -server : Server
        -authService : AuthService
        -vehicleService : VehicleService
        -employeeService : EmployeeService
        -departmentService : DepartmentService
        -assignmentService : AssignmentService
        +run()
        +pushToClient(push)
        -handleRequest(Request) Response
    }
    class VehicleService {
        +getAll() List
        +getById(id) Vehicle
        +create(v) Vehicle
        +update(v) Vehicle
        +search(s) List
    }
    class EmployeeService {
        +getAll() List
        +create(e) Employee
        +update(e) Employee
    }
    class AuthService {
        +login(u, p) User
    }
    class DepartmentService {
        +getAll() List
    }
    class AssignmentService {
        +assign(vid, eid) VehicleAssignment
        +unassign(vid)
        +getActiveForVehicle(vid) VehicleAssignment
    }

    %% ── DAO INTERFACES ──────────────────────────
    class VehicleDao {
        <<interface>>
        +getAll() List
        +getById(id) Vehicle
        +create(v) Vehicle
        +update(v) Vehicle
        +search(s) List
    }
    class EmployeeDao {
        <<interface>>
        +getAll() List
        +create(e) Employee
        +update(e) Employee
    }
    class SqlVehicleDao {
        +getAll() List
        +create(v) Vehicle
        +update(v) Vehicle
        +search(s) List
    }
    class SqlEmployeeDao {
        +getAll() List
        +create(e) Employee
        +update(e) Employee
    }

    %% ── RELATIONER ──────────────────────────────
    LoginController --> LoginViewModel
    VehicleListController --> VehicleListViewModel
    LoginViewModel --> AppContext
    VehicleListViewModel --> AppContext
    EmployeeListViewModel --> AppContext
    AppContext --> ServerConnection

    Server --> ClientHandler
    ClientHandler --> VehicleService
    ClientHandler --> EmployeeService
    ClientHandler --> AuthService
    ClientHandler --> DepartmentService
    ClientHandler --> AssignmentService
    VehicleService --> VehicleDao
    EmployeeService --> EmployeeDao
    VehicleDao <|.. SqlVehicleDao
    EmployeeDao <|.. SqlEmployeeDao
```
