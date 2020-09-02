# Pogmatic app backend

## Config

Before you build or deploy this app locally, you should start a local MySQL server, and create two MySQL schemas (one for the integration tests, and one for the deployed application).

Here is the drop-create command for the schemas:

```SQL
DROP SCHEMA `progmappbe`;
CREATE SCHEMA `progmappbe` DEFAULT CHARACTER SET utf8 COLLATE utf8_hungarian_ci;

DROP SCHEMA `progmappbetest`;
CREATE SCHEMA `progmappbetest` DEFAULT CHARACTER SET utf8 COLLATE utf8_hungarian_ci;
```

Then you should configure some environment variables for the application.properties file.
You can do it on the operational system level, or you can use your IDE (e.g. in IntelliJ it is in Run -> Edit configurations -> Override parameters).

Properties to configure:
```
MYSQL_USER_FOR_PROGMAPPBE=<Username for your local MySQL>
MYSQL_PWD_FOR_PROGMAPPBE=<Password for your local MySQL>
PROGMAPP_DEFAULT_ADMIN_PASSWORD=<Password for the initial admin user created during the first deoloy>
//These ones are only needed for e-mail sending
MAIL_PROGMATIC_USERNAME=<A valid username for an smtp mail server>
MAIL_PROGMATIC_PASSWORD=<A valid password for the above username>
```
## Build, deploy

Run
```
mvn clean install
```
this will create a jar file in target direcotry, which you can run with `java -jar`.

You can also run the class `com.progmatic.progmappbe.ProgmappbeApplication` directly from your preferred IDE.

## Test
Use the Postman collection file within /doc directory to test the deployed application.