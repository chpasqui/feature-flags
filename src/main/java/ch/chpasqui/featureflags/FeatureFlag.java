package ch.chpasqui.featureflags;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class FeatureFlag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(name = "flag_key", unique = true, nullable = false)
	private String key;

	private boolean enabled;

	@Min(0)
	@Max(100)
	private int rolloutPercentage;

	// Constructors
	public FeatureFlag() {
	}

	public FeatureFlag(String key, boolean enabled, int rolloutPercentage) {
		this.key = key;
		this.enabled = enabled;
		this.rolloutPercentage = rolloutPercentage;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getRolloutPercentage() {
		return rolloutPercentage;
	}

	public void setRolloutPercentage(int rolloutPercentage) {
		this.rolloutPercentage = rolloutPercentage;
	}

}