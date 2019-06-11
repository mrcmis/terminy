package com.fis.is.terminy.models;

import javax.persistence.*;

@Entity
public class CompanyWorkplace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;


    public CompanyWorkplace(){}
    public Long getId() {return this.id;}
    public void setId(Long id) {this.id = id;}

    public Company getCompany() {return company;}
    public void setCompany(Company company) {this.company = company;}

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

}
