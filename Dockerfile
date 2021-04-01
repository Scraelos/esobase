# Stage that builds the application, a prerequisite for the running stage
FROM maven:3-jdk-8 as build
#RUN curl -sL https://deb.nodesource.com/setup_12.x | bash -
#RUN apt-get update -qq && apt-get install -qq --no-install-recommends nodejs

# Stop running as root at this point
RUN useradd -m myuser
WORKDIR /usr/src/app/
RUN chown myuser:myuser /usr/src/app/
USER myuser

# Copy pom.xml and prefetch dependencies so a repeated build can continue from the next step with existing dependencies
COPY --chown=myuser pom.xml ./
RUN mvn dependency:go-offline -Pproduction

# Copy all needed project files to a folder
COPY --chown=myuser:myuser src src
#COPY --chown=myuser:myuser frontend frontend
#COPY --chown=myuser:myuser package.json pnpm-lock.yaml webpack.config.js ./
#COPY --chown=myuser:myuser package.json webpack.config.js ./

# We want war package because themes folder not included in jar
RUN sed -i "s|<packaging>jar</packaging>|<packaging>war</packaging>|g" pom.xml
# Build the production package, assuming that we validated the version before so no need for running tests again
RUN mvn clean package -DskipTests

# Running stage: the part that is used for running the application
FROM openjdk:8
# Timezone
RUN apt-get update && apt-get install -y --no-install-recommends \
    locales \
    locales-all
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY --from=build /usr/src/app/target/*.war /usr/app/app.jar
RUN useradd -m myuser
USER myuser
# Set the locale
ENV LANG ru_RU.UTF-8
ENV LANGUAGE ru_RU:ru
ENV LC_ALL ru_RU.UTF-8
CMD java -jar /usr/app/app.jar