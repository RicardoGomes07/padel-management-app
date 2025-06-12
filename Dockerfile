FROM openjdk:22
WORKDIR /usr/app
COPY ./static-content ./static-content
COPY ./build/libs ./libs
CMD ["java", "-jar", "./libs/2425-2-LEIC42D-G10.jar"]