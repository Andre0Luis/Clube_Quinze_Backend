package br.com.clube_quinze.api.repository;

import br.com.clube_quinze.api.model.feedback.Feedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Page<Feedback> findByUserId(Long userId, Pageable pageable);

    Optional<Feedback> findByAppointmentIdAndUserId(Long appointmentId, Long userId);

    boolean existsByAppointmentId(Long appointmentId);

    @Query("select avg(f.rating) from Feedback f where f.user.id = :userId")
    Double findAverageRatingForUser(@Param("userId") Long userId);

    @Query("select f.appointment.serviceType as service, avg(f.rating) as average from Feedback f group by f.appointment.serviceType")
    List<FeedbackAverageView> findAverageRatingByService();

    interface FeedbackAverageView {
        String getService();

        Double getAverage();
    }
}
