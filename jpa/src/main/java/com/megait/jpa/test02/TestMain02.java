package com.megait.jpa.test02;//package com.megait.jpa;

import com.megait.jpa.domain.Book;
import com.megait.jpa.domain.BookDetail;
import com.megait.jpa.domain.Category;
import com.megait.jpa.domain.Member;
import com.megait.jpa.domain.Publisher;
import com.megait.jpa.domain.Team;
import com.megait.jpa.domain.Writer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

//@SpringBootApplication
public class TestMain02 {

    public static void main(String[] args) {

        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("myunit");
        EntityManager em = factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();

            Publisher publisher1 = new Publisher();
            publisher1.setName("한빛출판사");

            Publisher publisher2 = new Publisher();
            publisher2.setName("영진출판사");

            Writer writer1 = new Writer();
            writer1.setName("Kim");
            writer1.setAge(35);
            writer1.setPublisher(publisher1);

            Writer writer2 = new Writer();
            writer2.setName("Lee");
            writer2.setAge(29);
            writer2.setPublisher(publisher2);

            BookDetail detail1 = new BookDetail();
            detail1.setSummary("it's fun comic book");
            detail1.setPage(300);

            BookDetail detail2 = new BookDetail();
            detail2.setSummary("it's horror book");
            detail2.setPage(290);

            BookDetail detail3 = new BookDetail();
            detail3.setSummary("it's mystery book");
            detail3.setPage(590);

            Book book1 = new Book();
            book1.setTitle("comic book");
            book1.setCategory(Category.COMIC);
            book1.setBookDetail(detail1);
            book1.setWriter(writer1);

            Book book2 = new Book();
            book2.setTitle("horror book");
            book2.setCategory(Category.HORROR);
            book2.setBookDetail(detail2);
            book2.setWriter(writer2);

            Book book3 = new Book();
            book3.setTitle("mystery book");
            book3.setCategory(Category.MYSTERY);
            book3.setBookDetail(detail3);
            book3.setWriter(writer2);

            em.persist(book1);
            em.persist(book2);
            em.persist(book3);

            em.remove(book1);

            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            factory.close();
        }

    }

}
