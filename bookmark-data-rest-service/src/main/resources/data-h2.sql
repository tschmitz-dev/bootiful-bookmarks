/*
 Sample data for bookmark service.

 The data will be inserted to H2 database during spring boot application start. Name of file (data-h2.sql) correspondents
 to the 'spring.datasource.platform' property in application.yml.
 */

insert into bookmark (title,href,user_id) values('Spring Initializr','https://start.spring.io/','demo');
insert into bookmark (title,href,user_id) values('Maven','https://maven.apache.org/guides/index.html','demo');
insert into bookmark (title,href,user_id) values('Spring Maven Plugin','https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/maven-plugin/','demo');
insert into bookmark (title,href,user_id) values('Baeldung | Java, Spring and Web Development tutorials','https://www.baeldung.com/','demo');

insert into bookmark (title,href,user_id) values('Spring Data REST Security','https://docs.spring.io/spring-data/rest/docs/current/reference/html/#security','user');
insert into bookmark (title,href,user_id) values('Spring Initializr','https://start.spring.io/','user');
insert into bookmark (title,href,user_id) values('Maven','https://maven.apache.org/guides/index.html','user');
insert into bookmark (title,href,user_id) values('Spring Framework Testing','https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/testing.html','user');

insert into tag (title) values ('Spring Framework');
insert into tag (title) values ('Maven');

insert into bookmarks_tags (bookmark_id, tag_id) select id, (select id from tag where title = 'Spring Framework') from bookmark where title like 'Spring%';
insert into bookmarks_tags (bookmark_id, tag_id) select id, (select id from tag where title = 'Maven') from bookmark where title like 'Maven%';