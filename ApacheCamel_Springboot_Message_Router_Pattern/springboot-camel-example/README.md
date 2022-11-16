

## Request:
~~~
curl --location --request POST 'http://localhost:9090/services/camel' \
--header 'accept: application/json' \
--header 'X-TASK-NAME: INVOICE' \
--header 'Content-Type: application/json' \
--data-raw '{
    "origin": "CHENNAI",
    "destination": "MUMBAI",
    "date": "20/11/2022",
    "shipper": "ABC",
    "consignee": "XYZ"
}'
~~~
~~~
curl --location --request GET 'http://localhost:9090/services/refresh/runtime_route_config'
~~~
## Health Check
~~~
curl --location --request GET 'http://localhost:9090/actuator/health'

~~~

Sample Rules Json
~~~
{
    "tasks": [
        {
            "taskName": "BOOKING",
            "kafkaDetails": {
                "bootstrapServer": "192.168.29.247:9092",
                "topic": "booking_topic"
            }
        },
        {
            "taskName": "INVOICE",
            "kafkaDetails": {
                "bootstrapServer": "192.168.29.247:9092",
                "topic": "invoice_topic"
            }
        }
    ]
}
~~~