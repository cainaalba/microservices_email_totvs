package com.vda.email.repo;

import com.vda.email.model.SF2Model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SF2Repo extends JpaRepository<SF2Model, String> {
}
