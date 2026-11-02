package ch.chpasqui.featureflags;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/api/flags")
public class FeatureFlagController {
	private final FeatureFlagRepository repository;

	public FeatureFlagController(FeatureFlagRepository repository){
		this.repository = repository;
	}

	private FeatureFlag findOrThrow(String key) {
		return repository.findByKey(key)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feature flag with key " + key + " not found"));
	}
	
	@GetMapping
	public List<FeatureFlag> list() {
		return repository.findAll();
	}

	@GetMapping("/{key}")
	public FeatureFlag get(@PathVariable String key){
		return findOrThrow(key);
	}

	@GetMapping("/{key}/evaluate")
	public boolean evaluate(@PathVariable String key, @RequestParam String userId){
		FeatureFlag flag = findOrThrow(key);
		return flag.isEnabledFor(userId);
	}

	@PostMapping 
	@ResponseStatus(HttpStatus.CREATED)
	public FeatureFlag create(@Valid @RequestBody FeatureFlag flag){
		if (repository.findByKey(flag.getKey()).isPresent())
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Feature flag already exists");
		return repository.save(flag);
	}

	@PutMapping("/{key}")
	public FeatureFlag update(@PathVariable String key, @Valid @RequestBody FeatureFlag body){
		FeatureFlag flag = findOrThrow(key);
		flag.setEnabled(body.isEnabled());
		flag.setRolloutPercentage(body.getRolloutPercentage());
		return repository.save(flag);
	}

	@DeleteMapping("/{key}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String key){
		FeatureFlag flag = findOrThrow(key);
		repository.delete(flag);
	}
}
