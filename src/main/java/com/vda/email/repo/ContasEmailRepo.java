package com.vda.email.repo;

import com.vda.email.model.ContasEmailModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContasEmailRepo extends JpaRepository<ContasEmailModel, Integer> {
    ContasEmailModel findAllByFilial(String filial);
}
