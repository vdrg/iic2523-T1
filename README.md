# iic2523-T1

## Build docker image

```bash
docker build --tag rmi .
```

## Usage

Start the server (Master):

```bash
docker run -i -p 1099:1099 rmi ./master.sh
# Output
# IP: ...
# PORT: ...
```

Run the client (Worker):

```bash
docker run rmi ./worker.sh <IP> <PROBABILITY>
```

Where IP is the address of the docker container for the master process, and PROBABILITY is the probability of the worker returning a wrong result.

Once some clients are connected, enter a number in the server to calculate the result.
