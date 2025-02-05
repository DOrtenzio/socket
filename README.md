# SOCKET 1 (esPizze/)

> ## PIZZA
Partendo dal semplice visto in classe scrivere un applicazione client server in cui

CLIENT
- si collega al server
- richiede le pizze disponibili al server
- sceglie una pizza tra quelle disponibili e la richiede al server
- aspetta conferma che la pizza venga effettivamente fornita (come messaggio dal server)
- dopo aver avuto conferma della pizza termina

SERVER
- gestisce un client alla volta
- una volta che un client si collega rimane in attesa di un messaggio
- se il messaggio è richiede lista pizze restituisce la lista delle pizze
- se il messaggio è scegliere una pizza specifica la concede sempre restituendo una pizza specifica
- rimane sempre attivo per nuovi client

# SOCKET 2 (esPizze_JSON/)
> ## PIZZA
Modifica il precedente aggiungendo la comunicazione con JSON
