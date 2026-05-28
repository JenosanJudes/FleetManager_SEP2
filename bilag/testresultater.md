# Testresultater – Fleet Manager

Alle unit tests køres i IntelliJ via JUnit 5.
Testmappen ligger under `test/server/service/`.

For at køre dem:
1. Højreklik på `test/`-mappen → Mark Directory as → Test Sources Root
2. Højreklik på en testfil → Run

Alle tests kørte grønt uden fejl.

---

## VehicleServiceTest – 10 tests

| Testnavn | Beskrivelse | Resultat |
|----------|-------------|----------|
| `create_withValidLicensePlate_returnsSavedVehicle` | Gyldig bil oprettes og returneres med id > 0 | ✅ Grøn |
| `create_withEmptyLicensePlate_throwsIllegalArgumentException` | Tomt reg.nr. giver fejl | ✅ Grøn |
| `create_withNullLicensePlate_throwsIllegalArgumentException` | Null reg.nr. giver fejl | ✅ Grøn |
| `create_withBlankLicensePlate_throwsIllegalArgumentException` | Kun mellemrum giver fejl | ✅ Grøn |
| `search_withNull_returnsAllVehicles` | Null returnerer alle biler | ✅ Grøn |
| `search_withEmptyString_returnsAllVehicles` | Tom streng returnerer alle biler | ✅ Grøn |
| `search_withBlankString_returnsAllVehicles` | Kun mellemrum returnerer alle biler | ✅ Grøn |
| `search_withPartialMatch_returnsMatchingVehicles` | Delvis match finder rigtig bil | ✅ Grøn |
| `search_withNoMatch_returnsEmptyList` | Ingen match giver tom liste | ✅ Grøn |
| `update_withValidLicensePlate_returnsUpdatedVehicle` | Bil opdateres korrekt | ✅ Grøn |

---

## EmployeeServiceTest – 5 tests

| Testnavn | Beskrivelse | Resultat |
|----------|-------------|----------|
| `create_withValidName_returnsSavedEmployee` | Gyldig medarbejder oprettes med id > 0 | ✅ Grøn |
| `create_withEmptyName_throwsIllegalArgumentException` | Tomt navn giver fejl | ✅ Grøn |
| `create_withNullName_throwsIllegalArgumentException` | Null navn giver fejl | ✅ Grøn |
| `create_withBlankName_throwsIllegalArgumentException` | Kun mellemrum giver fejl | ✅ Grøn |
| `update_withValidName_returnsUpdatedEmployee` | Medarbejder opdateres korrekt | ✅ Grøn |

---

## AssignmentServiceTest – 7 tests

| Testnavn | Beskrivelse | Resultat |
|----------|-------------|----------|
| `assign_withValidIds_returnsAssignment` | Gyldig tildeling oprettes med unassignedAt = null | ✅ Grøn |
| `assign_withZeroVehicleId_throwsIllegalArgumentException` | vehicleId = 0 giver fejl (BVA) | ✅ Grøn |
| `assign_withNegativeVehicleId_throwsIllegalArgumentException` | Negativt vehicleId giver fejl | ✅ Grøn |
| `assign_withZeroEmployeeId_throwsIllegalArgumentException` | employeeId = 0 giver fejl (BVA) | ✅ Grøn |
| `unassign_withValidVehicleId_doesNotThrow` | Gyldig fratagelse kaster ingen fejl | ✅ Grøn |
| `unassign_withZeroVehicleId_throwsIllegalArgumentException` | vehicleId = 0 giver fejl (BVA) | ✅ Grøn |
| `unassign_withNegativeVehicleId_throwsIllegalArgumentException` | Negativt vehicleId giver fejl | ✅ Grøn |

---

**Total: 22 tests – alle grønne**
