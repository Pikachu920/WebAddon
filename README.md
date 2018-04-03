# WebAddon

Intuitive http server functionality for [Skript](https://www.github.com/bensku/Skript) using the [Spark Framework](https://github.com/perwendel/spark).  

Example:
```
open a web server on port 8080:
  get /users/getUser/%{_user}%:
    broadcast "%header ""skript"" of event-response%"
    set header "skript" of event-response to "true"
    broadcast "%header ""skript"" of event-response%"
    set body of event-response to "%ip of event-request% requested user ##%{_user}%, from the path %path of event-request%"

  post /users/updateName/%{_user}%:
    send back "updated %{_user}%'s name!"

  delete /users/delete/%{_user}%:
    send back "deleted %{_user}%!"

  put /users/addUser/%{_user}%:
    send back "added user %{_user}%"

  trace /users/trace/%{_user}%:
    send back "no clue what this is for but you traced %{_user}%"

  options /users/enableSounds/%{_user}%:
    send back "enabled sounds for %{_user}%"

  patch /users/patch/%{_user}%:
    send back "patched %{_user}%"

  head /users/head/%{_user}%:
    send back "here is %{_user}%'s head"

  connect /users/connect/%{_user}%:

    send back "you connected to %{_user}%"
```
