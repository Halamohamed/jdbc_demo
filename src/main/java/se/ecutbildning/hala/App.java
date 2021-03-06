package se.ecutbildning.hala;

import se.ecutbildning.hala.data.Database;
import se.ecutbildning.hala.data.PersonDaoJDBC;
import se.ecutbildning.hala.entity.Person;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SQLException {
        //Database.getConnection();
        //Database.altGetConnection();
        PersonDaoJDBC dao = new PersonDaoJDBC();
        //System.out.println(dao.findById(1));
        Person person = dao.findById(1).get();
        person.setBirthDate(LocalDate.parse("1986-12-24"));
        //System.out.println(dao.create(new Person("Imtinan","Mohammed","imtinan@gmail.com",LocalDate.parse("2000-01-01"))));
        //System.out.println(dao.updatePerson(person));
        //System.out.println(dao.deletePerson(2));

        //find by email:
        //System.out.println(dao.findByEmail("hala@gmail.com"));

        //find by lastName:
       // dao.findByLastName("Negash").forEach(System.out::println);

        //find by birthDate
        dao.findByBirthDate(LocalDate.parse("1999-12-31")).forEach(System.out::println);

    }
}
