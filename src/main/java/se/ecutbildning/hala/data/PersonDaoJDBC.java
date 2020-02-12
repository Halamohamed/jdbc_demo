package se.ecutbildning.hala.data;

import se.ecutbildning.hala.entity.Person;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * findbylastname(String lastname)
 * findbyemail(String email)
 * findbybirthdate(LocalDate birhdate)
 *
 * findByNameLikeIgnoreCase(String name)
 * findByBirthDateBetween(LocalDate start, LocalDate end)
 *
 * create TodoItemDao
 */

import static se.ecutbildning.hala.data.Database.getConnection;

public class PersonDaoJDBC {

    private static final String INSERT =
            "INSERT INTO person(firstname,lastname,email,birthdate) VALUES(?,?,?,?)";
    private static final String FIND_BY_ID =
            "SELECT * FROM person WHERE person_id = ?";
    private static final String UPDATE_PERSON =
            "UPDATE person SET firstname = ?, lastname = ?, email = ?, birthdate = ? WHERE person_id = ?";

    private static final String DELETE_PERSON =
            "DELETE FROM person WHERE person_id = ?";

    private static final String FIND_BY_EMAIL =
            "SELECT * FROM person WHERE email = ?";
    private static final String FIND_BY_LASTNAME =
            "SELECT * FROM person WHERE lastname = ?";

    public Person create(Person person){
        Connection connection= null;
        PreparedStatement statement = null;
        ResultSet keySet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,person.getFirstName()); // (?,?,?,?) -> (Nisse,?,?,?)
            statement.setString(2,person.getLastName());// (Nisse,?,?,?) -> (Nisse,Nilsson,?,?)
            statement.setString(3,person.getEmail()); // (Nisse,Nilsson,?,?) -> (Nisse,Nilsson,nisse@gmail.com,?)
            statement.setObject(4,person.getBirthDate());// (Nisse,Nilsson,Nilsson,nisse@gmail.com,?) -> (Nisse,Nilsson,nisse@gmail.com,9180-01-01)
            statement.execute();
            keySet = statement.getGeneratedKeys();
            while (keySet.next()){
                person = new Person(
                        keySet.getInt(1),
                        person.getFirstName(),
                        person.getLastName(),
                        person.getEmail(),
                        person.getBirthDate()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (keySet != null){
                    keySet.close();
                }
                if (statement != null){
                    statement.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return person;
    }

    private PreparedStatement create_findById(Connection connection,int personId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(FIND_BY_ID);
        statement.setInt(1,personId);
        return statement;
    }

    public Optional<Person> findById(int personId){
        Optional<Person> optional = Optional.empty();
        try(
                Connection connection = getConnection();
                PreparedStatement statement = create_findById(connection,personId);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()){
                optional = Optional.of(createPersonFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }return optional;
    }

    private Person createPersonFromResultSet(ResultSet resultSet) throws SQLException {
       return new Person(
                resultSet.getInt("person_id"),
                resultSet.getString("firstname"),
                resultSet.getString("lastname"),
                resultSet.getString("email"),
                resultSet.getObject("birthdate", LocalDate.class)
        );
    }

    public Person updatePerson(Person person){
        try ( Connection connection = getConnection();
              PreparedStatement statement = connection.prepareStatement(UPDATE_PERSON)

        ){
            statement.setString(1,person.getFirstName());
            statement.setString(2,person.getLastName());
            statement.setString(3,person.getEmail());
            statement.setObject(4,person.getBirthDate());
            statement.setInt(5,person.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }return person;
    }

    public boolean deletePerson(int personId){
        try(
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE_PERSON)
        ) {
            statement.setInt(1,personId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return !findById(personId).isPresent();

    }

    public Optional<Person> findByEmail(String email){
        Optional<Person> optional = Optional.empty();
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL);
                ResultSet resultSet = statement.executeQuery()
        ){
            while (resultSet.next()){
                optional = Optional.of(new Person(resultSet.getInt("person_id"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getString("email"),
                        resultSet.getObject("birthdate",LocalDate.class))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return optional;
    }

    public List<Person> findByLastName(String lastName){
        List<Person> people = new ArrayList<>();
        try(
               Connection connection = getConnection();
               PreparedStatement statement = connection.prepareStatement(FIND_BY_LASTNAME);
               ResultSet resultSet = statement.executeQuery()
                ){
            while (resultSet.next()){
                people.add(new Person(resultSet.getInt("person_id"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getString("email"),
                        resultSet.getObject("birthdate",LocalDate.class)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;
    }
}
