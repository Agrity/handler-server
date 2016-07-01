package models;

import com.avaje.ebean.Model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import play.data.validation.Constraints;

@MappedSuperclass
public class BaseModel extends Model {

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @Column(name = "created_at")
  public LocalDateTime createdAt;

  @Column(name = "updated_at")
  public LocalDateTime updatedAt;

  @PrePersist
  public void createdAt() {
    this.createdAt = this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  public void updatedAt() {
    this.updatedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  };

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
