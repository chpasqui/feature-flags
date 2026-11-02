package ch.chpasqui.featureflags;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/api/flags")
public class FeatureFlagController {
	private final FeatureFlagRepository repository;

	public FeatureFlagController(FeatureFlagRepository repository){
		this.repository = repository;
	}
	
	@GetMapping
	public List<FeatureFlag> list() {
		return repository.findAll();
	}

	@PostMapping 
	@ResponseStatus(HttpStatus.CREATED)
	public FeatureFlag create(@Valid @RequestBody FeatureFlag flag){
		return repository.save(flag);
	}
}
