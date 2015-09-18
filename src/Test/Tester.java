/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import Entities.Student;
import Entities.Studypoint;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author sebastiannielsen
 */
public class Tester {
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpqlDemoPU");
    static EntityManager em = emf.createEntityManager();
    public static void main(String[] args) {
        
        //<-- execute the 7 queries -->
        test();
        
        //<-- Test to create new Student -->
        System.out.println("");
        System.out.println("<---------- Test create Method ---------->");
        createNewStudent("Sebastian", "Nielsen");
        TypedQuery<Student> query = em.createNamedQuery("Student.findByFirstname", Student.class);
        query.setParameter("firstname", "Sebastian");
        query.setMaxResults(1);
        Student result = query.getSingleResult();
        System.out.println("Expected: Sebastian was: "+ result.getFirstname());
        
         //<-- Test addStudypoint -->
        System.out.println("");
        System.out.println("<---------- Test addStudypoint ---------->");
        Studypoint point = new Studypoint();
        point.setDescription("JPA Studypoint Ex");
        point.setMaxval(6);
        point.setScore(6);
        point.setStudentId(result);
        result.addStudypoint(point);
        em.getTransaction().begin();
        em.persist(point);
        em.persist(result);
        em.getTransaction().commit();
        
        Query query2 = em.createQuery("SELECT SUM(s.score) FROM Studypoint s where s.studentId = :studentid", Studypoint.class);
        query2.setParameter("studentid", result);
        long result2 = (long) query2.getSingleResult();
        System.out.println("Total stodypoint expected: 6 was: " +  result2);
        
        
    }
    
    public static void test(){
        System.out.println("<---------- Execute queries ---------->");
        //<-- Find all students -->
        TypedQuery<Student> query1 = em.createNamedQuery("Student.findAll", Student.class);
        List<Student> results1 = query1.getResultList();
        for(Student student: results1){
            System.out.println("Query1: " + student.getFirstname());
        }
        
        //<-- Find all students with firstname Jan -->
        TypedQuery<Student> query2 = em.createNamedQuery("Student.findByFirstname", Student.class);
        query2.setParameter("firstname", "jan");
        query2.setMaxResults(1);
        Student results2 = query2.getSingleResult();
        System.out.println("Query2: "+ results2.getFirstname());
        
        //<-- Find all students with lastname Olsen -->
        TypedQuery<Student> query3 = em.createNamedQuery("Student.findByLastname", Student.class);
        query3.setParameter("lastname", "Olsen");
        List<Student> results3 = query3.getResultList();
        for(Student student: results3){
            System.out.println("Query3: "+ student.getFirstname());
        }
        
        //<-- Find total studypoint sum for one Student -->
        Query query4 = em.createQuery("SELECT SUM(s.score) FROM Studypoint s where s.studentId = :studentid", Studypoint.class);
        Student s = em.find(Student.class, 1);
        query4.setParameter("studentid", s);
        long results4 = (long) query4.getSingleResult();
        System.out.println("Query4: " + s.getFirstname() + " totalscore: "+  results4);
        
        //<-- Find total studypoint sum for all students -->
        Query query5 = em.createQuery("SELECT SUM(s.score) FROM Studypoint s ", Studypoint.class);
        long results5 = (long) query5.getSingleResult();
        System.out.println("Query5: Totalscore for all students: "+ results5);
        
        //<-- Find student with greatest score -->
        Query query6 = em.createQuery("SELECT s.studentId, SUM(s.score) FROM Studypoint s GROUP BY s.studentId ORDER BY SUM(s.score) DESC ", Studypoint.class);
        query6.setMaxResults(1);
        List<Object[]> results6 = query6.getResultList();
        results6.stream().forEach((record) ->{
            Student stud = (Student)record[0];
            long score = (long)record[1];
            System.out.println("Query6: Greatets score: " + stud.getFirstname() +": "+score);
        });
        
        //<-- Find student with lowest score -->
        Query query7 = em.createQuery("SELECT s.studentId, SUM(s.score) FROM Studypoint s GROUP BY s.studentId ORDER BY SUM(s.score) ASC ", Studypoint.class);
        query7.setMaxResults(1);
        List<Object[]> results7 = query7.getResultList();
        results7.stream().forEach((record) ->{
            Student stud = (Student)record[0];
            long score = (long)record[1];
            System.out.println("Query7: Lowest score: " + stud.getFirstname() +": "+score);
        });
    }
     
    //<-- method to create new student -->
    public static void createNewStudent(String firstname, String lastname){
        Student s = new Student();
        s.setFirstname(firstname);
        s.setLastname(lastname);
        em.getTransaction().begin();
        em.persist(s);
        em.getTransaction().commit();
        
    }  
} 
    
    

