# iic2523-T1

## Build docker image

```bash
docker build --tag rmi .
```

## Usage

Start the server (Master):

```bash
docker run -i -p 1099:1099 rmi ./master.sh
```

Run the client (Worker):

```bash
docker run rmi ./worker.sh <HOST>
```

Where HOST is the address of the server.

Once some clients are connected, enter a number in the server to calculate the result.
