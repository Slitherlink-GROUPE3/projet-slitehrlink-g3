package com.tpgr3.backend.dto;

import java.util.List;

public class GrilleDTO {
    public int niveau;
    public int[][] valeurs;
    public List<CoordDTO> solution; // réponse complète
    public List<SlotDTO> joues;    // coups déjà placés au moment du chargement
}
