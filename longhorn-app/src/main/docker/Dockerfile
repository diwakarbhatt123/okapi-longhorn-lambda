FROM jetty:alpine

ADD *.war /var/lib/jetty/webapps/
ADD okapi-longhorn-configuration.xml /home/jetty/

RUN mkdir /home/jetty/Okapi-Longhorn-Files
RUN chown -R jetty:jetty /home/jetty

