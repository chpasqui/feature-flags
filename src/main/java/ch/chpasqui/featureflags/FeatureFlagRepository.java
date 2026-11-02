package ch.chpasqui.featureflags;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long>{
	Optional<FeatureFlag> findByKey(String key);
	
}
