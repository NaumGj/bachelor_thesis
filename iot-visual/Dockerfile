FROM ubuntu
 
RUN apt-get update -qq
RUN apt-get install -y build-essential nodejs npm nodejs-legacy vim
 
RUN mkdir /myangularapp
ADD iot-visual/www /myangularapp
WORKDIR /myangularapp
 
RUN npm install -g http-server
 
EXPOSE 8080
 
CMD ["http-server"]