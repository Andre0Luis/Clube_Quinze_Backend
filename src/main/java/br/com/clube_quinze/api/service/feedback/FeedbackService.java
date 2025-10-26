package br.com.clube_quinze.api.service.feedback;

import br.com.clube_quinze.api.dto.common.PageResponse;
import br.com.clube_quinze.api.dto.feedback.FeedbackAverageResponse;
import br.com.clube_quinze.api.dto.feedback.FeedbackRequest;
import br.com.clube_quinze.api.dto.feedback.FeedbackResponse;
import java.util.List;

public interface FeedbackService {

    FeedbackResponse submitFeedback(Long actorId, boolean privileged, FeedbackRequest request);

    PageResponse<FeedbackResponse> getMyFeedback(Long actorId, int page, int size);

    PageResponse<FeedbackResponse> getFeedback(Long userId, int page, int size);

    List<FeedbackAverageResponse> getAverageByService();

    Double getUserAverage(Long userId);
}
