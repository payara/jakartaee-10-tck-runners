# How to Run
1. Go to the tck-download/jakarta-websocket-tck folder that is on the root of the jakartaee-10-tck-runners project 
folder and execute `mvn clean install`
2. Return to the websocket-tck folder on the root of the jakartaee-10-tck-runners project folder
3. Execute the following command indicating the payara.home in case you are using local distribution and the 
Profile `mvn verify -Dpayara.home=x -Ppayara-server-managed` or `mvn verify -Dpayara.home=x -Ppayara-server-remote`