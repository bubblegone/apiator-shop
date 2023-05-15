package com.apiator.shop.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Item {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    @Size(min = 2, max = 64)
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String imageName;
    @Size(max = 512)
    private String description;
    @Size(min = 4, max = 4)
    @JsonIgnore
    private String ownerCard;
    private int count;
}

