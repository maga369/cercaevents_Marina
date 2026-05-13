package com.example.serveis;

import com.example.dao.UsuariDAO;
import com.example.model.Usuari;

/**
 * Servei d'autenticacio i estat de l'usuari connectat.
 */
public class ServeiUsuari {
    private static Usuari usuariActual;

    /**
     * Valida les credencials i desa l'usuari autenticat com a usuari actual.
     *
     * @param sUsuari nom d'usuari introduit
     * @param sContrasenya contrasenya en text pla introduida al login
     * @return usuari autenticat
     * @throws Exception si les credencials no son valides
     */
    public static Usuari existeixUsuari(String sUsuari, String sContrasenya) throws Exception {
        
            Usuari usuari = UsuariDAO.buscarUsuari(sUsuari, sContrasenya);
            if (usuari == null) {
                throw new Exception("Usuari o contrasenya incorrectes");
            }
            usuariActual = usuari;
            return usuari;
    }

    /**
     * Retorna l'usuari autenticat en la sessio actual.
     *
     * @return usuari actual o null si encara no s'ha iniciat sessio
     */
    public static Usuari getUsuariActual() {
        return usuariActual;
    }
}
