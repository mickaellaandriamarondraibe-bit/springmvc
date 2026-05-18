                     Creation demande (demande_status.status_id = Cree)
                                               |
                                               |
         Creation de devis (demande_status.status_id = DEC ET devis.type_id = Etude)  
                                               |
                                               ^ (Validation de l'etude : Accepter ou Refuser)
                                              / \
                                             /   \
      Refusé (demande_status.status_id = DER)     Accepté (demande_status.status_id = DEA)
                                                                                     |   
                                                                                     |
                                                            demande_status.status_id = DFC ET devis.type_id = Etude
                                                                                       |
                                                                                       ^ 
                                                                                      / \                  
                                                Refusé (demande_status.status_id = DFR)  Accepté (demande_status.status_id = DFA)
                                                                                                                |
                                                                                                                | 
                                                                                                  (demande_status.status_id = TT)  
                                                                                                                |
                                                                                                  (demande_status.status_id = TC)  



utilise onbleer