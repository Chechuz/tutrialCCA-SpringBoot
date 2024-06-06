package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Client get(Long id) {
        return this.clientRepository.findById(id).orElse(null);
    }

    @Override
    public List<Client> findAll() {
        return (List<Client>) this.clientRepository.findAll();
    }

    protected int itExist(List<Client> clients, ClientDto dto) {
        for (Client c : clients) {
            if (c.getName().equalsIgnoreCase(dto.getName())) {
                return 0; // Cliente encontrado
            }
        }
        return -1; // Cliente no encontrado
    }

    @Override
    public void save(Long id, ClientDto dto) throws Exception {

        Client client;

        if (id == null) {
            if (itExist(findAll(), dto) == 0) {
                throw new Exception("This client already exists");
            } else {
                client = new Client();
            }
        } else {
            if (itExist(findAll(), dto) == 0) {
                throw new Exception("This client already exists");
            } else {
                client = this.get(id);
            }
        }
        client.setName(dto.getName());
        this.clientRepository.save(client);
    }

    @Override
    public void delete(Long id) throws Exception {

        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }
        this.clientRepository.deleteById(id);

    }
}
