package models;

import com.avaje.ebean.Model;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;

import play.data.validation.Constraints;

@MappedSuperclass
public abstract class BaseModel extends Model {

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Column(name = "created_at")
  public LocalDateTime createdAt;

  @Column(name = "updated_at")
  public LocalDateTime updatedAt;

  @PrePersist
  public void createdAt() {
    this.createdAt = this.updatedAt = LocalDateTime.now(ZoneId.of("PST"));
  }

  @PreUpdate
  public void updatedAt() {
    this.updatedAt = LocalDateTime.now(ZoneId.of("PST"));
  }

  public Long getId() {
    return id;
  };

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public String getCreatedAtAsString() {
    return createdAt.toString();
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public String getUpdatedAtAsString() {
    return updatedAt.toString();
  }
}
