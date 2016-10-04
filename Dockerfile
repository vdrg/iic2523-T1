FROM openjdk:8

# Bundle app source
COPY . /src
RUN  cd /src && ./build.sh

WORKDIR /src

EXPOSE 1099
