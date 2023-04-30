package packages;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
        applicationContext.start();
        Entering entering = applicationContext.getBean(Entering.class);
        entering.start();
        }
        // сделать так, чтобы не кидались exceptions
    // удалить max capacity
    // соответсвие hall id и cinema id
}
