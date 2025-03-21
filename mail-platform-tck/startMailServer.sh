#!/bin/bash
docker run --name james-mail --rm -d -p $0:$0 -p $1:$1 --entrypoint=/bin/bash jakartaee/cts-mailserver:0.1 -c /root/startup.sh
sleep 10
docker exec -it james-mail /bin/bash -c /root/create_users.sh