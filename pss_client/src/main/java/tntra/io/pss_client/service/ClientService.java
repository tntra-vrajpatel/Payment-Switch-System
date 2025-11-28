package tntra.io.pss_client.service;
import tntra.io.pss_client.dto.ResponseDTO;

public interface ClientService {
    public ResponseDTO sendRequest(String json) ;
}
