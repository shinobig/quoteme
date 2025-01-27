package com.shinobig.quoteme.model;


import com.shinobig.quoteme.Quote;
import com.shinobig.quoteme.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class StartingDatabase {

  private List<User> allUsers;
  private SessionFactory factory;

  public StartingDatabase() {

    factory = new Configuration()
        .configure("hibernate.cfg.xml")
        .addAnnotatedClass(User.class)
        .addAnnotatedClass(Quote.class)
        .buildSessionFactory();

    Session session = factory.getCurrentSession();
    try {
      session.beginTransaction();
      allUsers = session
          .createQuery("from User")
          .getResultList();
      session.getTransaction().commit();
    } finally {
      session.close();
    }

  }

  public List<User> getAllUsers() {
    return allUsers;
  }

  public void setAllUsers(List<User> allUsers) {
    this.allUsers = allUsers;
  }

  public boolean createNewUser(User newUserToAdd){

    if(getSingleUser(newUserToAdd.getUsername()) != null){
      return false;
    } else {
      Session session = factory.getCurrentSession();
      try {
        session.beginTransaction();
       session.save(newUserToAdd);
        session.getTransaction().commit();
        System.out.println("User successfully saved");

      } finally {
        session.close();
        return true;
      }
    }

  }

  public User getSingleUser(String username) {
    for (User user : allUsers) {
      if (user.getUsername().equals(username)) {
        return user;
      }
    }
    return null;
  }


  public User isRightPassword(String username, String password) {
    User user = getSingleUser(username);
    if (user != null) {
      if (user.getPassword().equals(password)) {
        return user;
      }
    }
    return null;
  }

  public boolean addNewQuoteByUser(String username, Quote quoteToAdd) {

    User user = getSingleUser(username);

    if (user != null) {
      if (doesQuoteAlreadyExists(user, quoteToAdd)) {
        return false;
      } else {
        Session session = factory.getCurrentSession();
        try {
          session.beginTransaction();
          user.add(quoteToAdd);
          session.save(quoteToAdd);
          session.getTransaction().commit();

        } finally {
          session.close();
        }
        System.out.println("quote successfully added");
        return true;
      }
    } else {
      return false;
    }
  }


  public boolean doesQuoteAlreadyExists(User user, Quote quoteToCheck) {
    List<Quote> userQuotes = user.getUserQuotes();
    for (Quote quote : userQuotes) {
      if (quoteToCheck.getQuote().equals(quote.getQuote())) {
        return true;
      }
    }
    return false;
  }


  public boolean editQuoteByUser(Quote quoteToEdit, int quoteId){

      Session session = factory.getCurrentSession();
      try {
        session.beginTransaction();
        Quote dbQuote = session.get(Quote.class, quoteId);

        dbQuote.setQuote(quoteToEdit.getQuote());
        dbQuote.setAuthor(quoteToEdit.getAuthor());
        dbQuote.setSource(quoteToEdit.getSource());
        dbQuote.setCategory(quoteToEdit.getCategory());

        session.getTransaction().commit();

      } finally {
        session.close();
      }


    return false;

  }

}
