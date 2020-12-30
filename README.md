Step1. Navigate to zeebe-docker-compose/simple0monitore and issue docker-compose up command to start zeebe broker and monitor
    monitor is accessible at localhost:8082
Step 2.  Make kafka broker up 
Step  3.  make all 5 microservices order,checkout,payment,shipping,inventory up

Step 4. Place the order by localhost:8091

Visit the monitor localhost:8082 to view the work flow instance

Note: usefull links
https://github.com/berndruecker/flowing-retail
https://docs.zeebe.io/clients/other-clients/index.html