# Pogmatic app backend

## Config

Before you build or deploy this app locally, you should start a local MySQL server, and create a MySQL schema.

Here is the drop-create command for the schema:

```SQL
DROP SCHEMA `progmappbe`;
CREATE SCHEMA `progmappbe` DEFAULT CHARACTER SET utf8 COLLATE utf8_hungarian_ci;
```

Then you should configure some environment variables for the application.properties file.
You can do it on the operational system level, or you can use your IDE (e.g. in IntelliJ it is in Run -> Edit configurations -> Override parameters).

Properties to configure:
```
MYSQL_USER_FOR_PROGMAPPBE=<Username for your local MySQL>
MYSQL_PWD_FOR_PROGMAPPBE=<Password for your local MySQL>
PROGMAPP_DEFAULT_ADMIN_PASSWORD=<Password for the initial admin user created during the first deoloy>
//These ones are only needed for e-mail sending
GMAIL_PROGMATIC_USERNAME=<A valid Gmail username>
GMAIL_PROGMATIC_PASSWORD=<A valid Gmail password for the above username>
```
## Build, deploy

Run
```
mvn clean install
```
this will create a jar file in target direcotry, which you can run with `java -jar`.

You can also run the class `com.progmatic.progmappbe.ProgmappbeApplication` directly from your preferred IDE.

## Test
Use the Postman collection file whithin /doc directory to test the application.