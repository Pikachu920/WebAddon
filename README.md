# WebAddon

Intuitive http server functionality for [Skript](https://www.github.com/bensku/Skript) using the [Spark Framework](https://github.com/perwendel/spark).  

Example:
```
web server on port 8080:
  get /users/%{_user}%:
    set body of event-response to "%ip of event-request% requested user ##%{_user}%, from the path %path of event-request%"
  get *:
    send back "oh shit"
  post /postTest:
    send back "noted"
```
