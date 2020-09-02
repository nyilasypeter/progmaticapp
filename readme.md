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
PROGMAPP_ALLOWED_ORIGINS_SEMINCOLON_SEPARATED=<A semiconlon seprated list of allowed origins for CORS configuration>
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

### Deploy to Heroku

First create an app with heroku CLI in the app's root directory with.
```
heroku create
```
Before deploying to Heroku make sure to set the above environment variables (on Heroku they are called config vars) with heroku cli. The command is:
```
heorku config set name=value
```
Also create a MySql database (ClearDB is OK) on Heroku, and make sure to extend the database_url with these query parameters:
```
useUnicode=yes&characterEncoding=UTF-8
```
It can be done on the web interface at Settings / Config Vars / Reveal Config Vars.

Then app can be deployed directly to Heroku with 
```
git push heroku master
```


## Test
Use the Postman collection file within /doc directory to test the deployed application.