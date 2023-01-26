# User Manager

Der User Manager bietet dem Benutzer die Möglichkeit, diverse Funktionen durchzuführen.

Diese Funktionen sind:

Vor dem Login:

* Kreieren eines Users
* Mit einem vorhanden User anmelden

Nach dem Login:

* Passwort ändern
* User löschen
* Abmelden

Die Schnittstelle um diese Funktionen auszuführen wurde mit **Swagger** definiert. Mit REST API Befehlen kann die gewünschte Aktion ausgeführt werden. Die Route für die Funktionen lauten:

**Passwort ändern**
```
REST Methode: PUT
/api/v1/user/{username}/password
Change user password
```

**User erstellen**
```
REST Methode: POST
/api/v1/user
Create user
```
**Login**
```
REST Methode: POST
/api/v1/auth/login
Logs user into the system
```

**Logout**
```
REST Methode: POST
/api/v1/auth/logout
Logs out current logged in user session
```

**Get User**
```
REST Methode: GET
/api/v1/user/{username}
Get user by user name
```

**User löschen**
```
REST Methode: DELETE
REST Methode: /api/v1/user/{username}
Delete user
```

## Schnellstart
### SDK: Java 17
### Installation

```
mvn clean install
```

### Ausführen
'UserManagerApplication.main()' ausführen um den Server zu starten.

### Verwendung
* Browser starten
* Öffnen Sie http://localhost:8080/swagger.yaml
* Erstellen Sie einen Benutzer mit dem POST /user Endpunkt
* Melden Sie sich über den /login Endpunkt an, um ein Authentifizierungstoken zu erhalten
* Geben Sie das Token über die Schaltfläche "Authorize" für jede andere Anforderung ein.

## Tests

Der Test Suite wurde mithilfe von Spring erstellt. Die Organisation der Methoden befolgt das **Arrange** - **Act** - **Assert** pattern. Bei den Tests war es wichtig darauf zu achten, dass alle unabhängig voneinander ausgeführt werden konnten.

## Continious Integration (CI)

Für die CI wurden zwei Maven .yml Dateien in den .git Ordner hinzugefügt. Die in den Dateien abgebildeten Build Pipeline ist dafür zuständig, um automatisiert zu überprüfen, ob mehr als 60% Code coverage erreicht wurde.

## Feedback

Das Feedback ist im Großen und Ganzen sehr positiv ausgefallen. Es wurden kleinere Verbesserungen übernommen. Die Benennung der Tests wurde sprechender gemacht. Aber es wurde gegen die Verwendung einer MongoDB entschieden, da sich der Nutzen nicht erschließt.