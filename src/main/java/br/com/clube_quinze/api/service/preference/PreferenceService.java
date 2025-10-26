package br.com.clube_quinze.api.service.preference;

import br.com.clube_quinze.api.dto.preference.PreferenceRequest;
import br.com.clube_quinze.api.dto.preference.PreferenceResponse;
import java.util.List;

public interface PreferenceService {

    List<PreferenceResponse> listPreferences(Long actorId, boolean privileged, Long userId);

    PreferenceResponse upsertPreference(Long actorId, boolean privileged, Long userId, PreferenceRequest request);

    PreferenceResponse updatePreference(Long preferenceId, Long actorId, boolean privileged, PreferenceRequest request);

    void deletePreference(Long preferenceId, Long actorId, boolean privileged);
}
