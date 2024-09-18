FROM nginx:mainline-alpine
RUN rm /etc/nginx/conf.d/*
ADD jobjar.conf /etc/nginx/conf.d/default.conf
RUN rm /usr/share/nginx/html/index.html
#ADD index.html /usr/share/nginx/html/

RUN mkdir -p /usr/share/nginx/html/flink-jobjars
ADD enriched-orders-jobs-1.0.0.jar /usr/share/nginx/html/flink-jobjars
#ADD enriched-orders-jobs.properties /flink-jobjars
