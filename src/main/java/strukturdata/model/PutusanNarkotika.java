package strukturdata.model;

import java.time.LocalDate;

/**
 * Model data putusan perkara tindak pidana narkotika PN Jakarta Selatan (21 kolom).
 */
public class PutusanNarkotika {
    private String caseId;
    private String nomorPerkara;
    private String pengadilan;
    private LocalDate tanggalPutusan;
    private String terdakwaKode;
    private String jenisNarkotika;
    private Double beratBarangBuktiGram; // Nullable
    private String pasalDakwaan;
    private String pasalTerbukti;
    private Integer tuntutanBulan;        // Nullable
    private Integer vonisBulan;
    private Long dendaRupiah;            // Nullable
    private String barangBukti;
    private String keadaanMemberatkan;
    private String keadaanMeringankan;
    private String sourcePages;
    private String bentukDakwaan;
    private String tuntutanJenis;
    private String dendaCatatan;
    private String notes;
    private String beratBasis;

    public PutusanNarkotika(String caseId, String nomorPerkara, String pengadilan, LocalDate tanggalPutusan,
                            String terdakwaKode, String jenisNarkotika, Double beratBarangBuktiGram,
                            String pasalDakwaan, String pasalTerbukti, Integer tuntutanBulan, Integer vonisBulan,
                            Long dendaRupiah, String barangBukti, String keadaanMemberatkan,
                            String keadaanMeringankan, String sourcePages, String bentukDakwaan,
                            String tuntutanJenis, String dendaCatatan, String notes, String beratBasis) {
        this.caseId = caseId;
        this.nomorPerkara = nomorPerkara;
        this.pengadilan = pengadilan;
        this.tanggalPutusan = tanggalPutusan;
        this.terdakwaKode = terdakwaKode;
        this.jenisNarkotika = jenisNarkotika;
        this.beratBarangBuktiGram = beratBarangBuktiGram;
        this.pasalDakwaan = pasalDakwaan;
        this.pasalTerbukti = pasalTerbukti;
        this.tuntutanBulan = tuntutanBulan;
        this.vonisBulan = vonisBulan;
        this.dendaRupiah = dendaRupiah;
        this.barangBukti = barangBukti;
        this.keadaanMemberatkan = keadaanMemberatkan;
        this.keadaanMeringankan = keadaanMeringankan;
        this.sourcePages = sourcePages;
        this.bentukDakwaan = bentukDakwaan;
        this.tuntutanJenis = tuntutanJenis;
        this.dendaCatatan = dendaCatatan;
        this.notes = notes;
        this.beratBasis = beratBasis;
    }

    // Constructor backward compatible 16 kolom jika dibutuhkan
    public PutusanNarkotika(String caseId, String nomorPerkara, String pengadilan, LocalDate tanggalPutusan,
                            String terdakwaKode, String jenisNarkotika, Double beratBarangBuktiGram,
                            String pasalDakwaan, String pasalTerbukti, Integer tuntutanBulan, Integer vonisBulan,
                            Long dendaRupiah, String barangBukti, String keadaanMemberatkan,
                            String keadaanMeringankan, String sourcePages) {
        this(caseId, nomorPerkara, pengadilan, tanggalPutusan, terdakwaKode, jenisNarkotika, beratBarangBuktiGram,
                pasalDakwaan, pasalTerbukti, tuntutanBulan, vonisBulan, dendaRupiah, barangBukti,
                keadaanMemberatkan, keadaanMeringankan, sourcePages, "", "", "", "", "");
    }

    public String getCaseId() { return caseId; }
    public String getNomorPerkara() { return nomorPerkara; }
    public String getPengadilan() { return pengadilan; }
    public LocalDate getTanggalPutusan() { return tanggalPutusan; }
    public String getTerdakwaKode() { return terdakwaKode; }
    public String getJenisNarkotika() { return jenisNarkotika; }
    public Double getBeratBarangBuktiGram() { return beratBarangBuktiGram; }
    public String getPasalDakwaan() { return pasalDakwaan; }
    public String getPasalTerbukti() { return pasalTerbukti; }
    public Integer getTuntutanBulan() { return tuntutanBulan; }
    public Integer getVonisBulan() { return vonisBulan; }
    public Long getDendaRupiah() { return dendaRupiah; }
    public String getBarangBukti() { return barangBukti; }
    public String getKeadaanMemberatkan() { return keadaanMemberatkan; }
    public String getKeadaanMeringankan() { return keadaanMeringankan; }
    public String getSourcePages() { return sourcePages; }
    public String getBentukDakwaan() { return bentukDakwaan; }
    public String getTuntutanJenis() { return tuntutanJenis; }
    public String getDendaCatatan() { return dendaCatatan; }
    public String getNotes() { return notes; }
    public String getBeratBasis() { return beratBasis; }

    public void setCaseId(String caseId) { this.caseId = caseId; }
    public void setNomorPerkara(String nomorPerkara) { this.nomorPerkara = nomorPerkara; }
    public void setPengadilan(String pengadilan) { this.pengadilan = pengadilan; }
    public void setTanggalPutusan(LocalDate tanggalPutusan) { this.tanggalPutusan = tanggalPutusan; }
    public void setTerdakwaKode(String terdakwaKode) { this.terdakwaKode = terdakwaKode; }
    public void setJenisNarkotika(String jenisNarkotika) { this.jenisNarkotika = jenisNarkotika; }
    public void setBeratBarangBuktiGram(Double beratBarangBuktiGram) { this.beratBarangBuktiGram = beratBarangBuktiGram; }
    public void setPasalDakwaan(String pasalDakwaan) { this.pasalDakwaan = pasalDakwaan; }
    public void setPasalTerbukti(String pasalTerbukti) { this.pasalTerbukti = pasalTerbukti; }
    public void setTuntutanBulan(Integer tuntutanBulan) { this.tuntutanBulan = tuntutanBulan; }
    public void setVonisBulan(Integer vonisBulan) { this.vonisBulan = vonisBulan; }
    public void setDendaRupiah(Long dendaRupiah) { this.dendaRupiah = dendaRupiah; }
    public void setBarangBukti(String barangBukti) { this.barangBukti = barangBukti; }
    public void setKeadaanMemberatkan(String keadaanMemberatkan) { this.keadaanMemberatkan = keadaanMemberatkan; }
    public void setKeadaanMeringankan(String keadaanMeringankan) { this.keadaanMeringankan = keadaanMeringankan; }
    public void setSourcePages(String sourcePages) { this.sourcePages = sourcePages; }
    public void setBentukDakwaan(String bentukDakwaan) { this.bentukDakwaan = bentukDakwaan; }
    public void setTuntutanJenis(String tuntutanJenis) { this.tuntutanJenis = tuntutanJenis; }
    public void setDendaCatatan(String dendaCatatan) { this.dendaCatatan = dendaCatatan; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setBeratBasis(String beratBasis) { this.beratBasis = beratBasis; }

    @Override
    public String toString() {
        return "PutusanNarkotika{" +
                "caseId='" + caseId + '\'' +
                ", nomorPerkara='" + nomorPerkara + '\'' +
                ", jenisNarkotika='" + jenisNarkotika + '\'' +
                ", vonisBulan=" + vonisBulan +
                ", beratBasis='" + beratBasis + '\'' +
                '}';
    }
}
