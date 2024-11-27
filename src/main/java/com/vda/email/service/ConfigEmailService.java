package com.vda.email.service;

import com.vda.email.model.ContasEmailModel;
import com.vda.email.repo.ContasEmailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigEmailService {
    @Autowired
    private final ContasEmailRepo contasEmailRepo;

    public ConfigEmailService(ContasEmailRepo contasEmailRepo) {
        this.contasEmailRepo = contasEmailRepo;
    }

    public ContasEmailModel buscaConfigEmail(String filial) {
        return contasEmailRepo.findAllByFilial(filial.substring(0,4));
    }
}
