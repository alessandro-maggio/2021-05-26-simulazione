package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.interfaces.DHPublicKey;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.TestDao;
import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private List<String> cities;
	private Graph<Business, DefaultWeightedEdge> grafo;
	private List<Business> vertici;
	private Map<String, Business> idMap;
	private List<Coppia> archi;
	private List<Business> migliori;
	
	private List<Business> percorsoMinimo= new ArrayList<>();
	private Business start;
	private Business end;
	private double soglia;
	private Business best;
	
	
	public Model() {
		this.dao= new YelpDao();
		this.cities= dao.getAllCitta();
		this.idMap= dao.getAllBusiness();
		
	}
	
	
	public String creaGrafo(String city, int anno) {
		
		this.grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		vertici= dao.getVertici(idMap, city, anno);
		
		
		Graphs.addAllVertices(this.grafo, vertici);
		
		archi= dao.getArchi(city, anno, idMap);
		
		for(Coppia c: archi) {
			
			Graphs.addEdge(this.grafo, c.getB1(), c.getB2(), c.getPeso());
			
		}
		
		String s= "GRAFO CREATO!\n#VERTICI: "+this.grafo.vertexSet().size()+"\n#ARCHI: "+this.grafo.edgeSet().size();
		
		return s;
		
		
	}
	

	public List<String> getAllCitta() {
		
		return cities;
	}
	
	
	public String migliore() {
		
		migliori= new ArrayList<>();
		
		double in;
		double out;
		double max=-1;
		best=null;
		
		for(Business b: vertici) {
			in=0;
			out=0;
			for(DefaultWeightedEdge b1: this.grafo.incomingEdgesOf(b)) {
				in+=this.grafo.getEdgeWeight(b1);
			}
			for(DefaultWeightedEdge b2: this.grafo.outgoingEdgesOf(b)) {
				out+= this.grafo.getEdgeWeight(b2);
			}
			
			if((in-out) >= max) {
				max=(in-out);
				best= b;
				//b.setMassimo(max);
				migliori.add(b);
			}

		}
		
		//Collections.sort(migliori);
		
		
		
		
		String s= "LOCALE MIGLIORE: "+best.getBusinessName();
		
		return s;
		
	}
	
	public Business getBest() {
		return best;
	}
	
	public List<Business> getVertici(){
		return vertici;
	}
	
	
	public List<Business> cercaPercorso(Business start, Business end, Double soglia){
		
		percorsoMinimo= new ArrayList<>();
		List<Business> parziale= new ArrayList<>();
		this.start= start;
		this.end= end;
		this.soglia= soglia;
		parziale.add(start);
		cerca(parziale);

		return percorsoMinimo;
		
	}

	private void cerca(List<Business> parziale) {
		
		//condizione di terminazione
		if(parziale.get(parziale.size()-1).equals(end)) {
			if(parziale.size()<percorsoMinimo.size() || percorsoMinimo.isEmpty()) {
				percorsoMinimo= new ArrayList<>(parziale);
			}
			return;
		}
		Business ultimoInserito= parziale.get(parziale.size()-1);
		
		Set<DefaultWeightedEdge> vicini= this.grafo.outgoingEdgesOf(ultimoInserito);
		
		for(DefaultWeightedEdge v: vicini) {
			if(!parziale.contains(this.grafo.getEdgeTarget(v))) {
			if(this.grafo.getEdgeWeight(v) >= soglia) { 
				parziale.add(this.grafo.getEdgeTarget(v));
				cerca(parziale);
				parziale.remove(this.grafo.getEdgeTarget(v));
				
			}
			}
		}
	
	
	}
	
}
