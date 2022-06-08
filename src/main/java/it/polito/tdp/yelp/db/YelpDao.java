package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Coppia;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public Map<String, Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		Map<String, Business> result = new HashMap<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.put(business.getBusinessId(), business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getAllCitta(){
		
		String sql = "select distinct city "
				+ "FROM business";
		
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(res.getString("city"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public List<Business> getVertici(Map<String, Business> idMap, String city, int anno){
		
		String sql = "select distinct r.`business_id` as id "
				+ "from reviews r, business b "
				+ "where r.`business_id`= b.`business_id` and b.`city`= ? and YEAR(r.`review_date`) = ? "
				+ "group by r.`business_id` "
				+ "having count(*) >0";
		
		List<Business> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
	
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			st.setInt(2, anno);
			ResultSet res = st.executeQuery();
			while (res.next()) {
	
				result.add(idMap.get(res.getString("id")));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
			
	}
	
	public List<Coppia> getArchi(String city, int anno, Map<String, Business> idMap){
		
		String sql = "select r1.`business_id` as id1 , r2.`business_id` as id2 , (avg(r1.`stars`)-avg(r2.`stars`)) as peso "
				+ "from business b1, business b2, reviews r1, reviews r2 "
				+ "where b1.`business_id`= r1.`business_id` and r2.`business_id` = b2.`business_id` and b1.`city`= ? and b1.`city` = b2.`city` and YEAR(r1.`review_date`) = ? and YEAR(r1.`review_date`) = YEAR(r2.`review_date`) "
				+ "group by r1.`business_id`, r2.`business_id` "
				+ "having count(*)>0";
		
		List<Coppia> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
	
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			st.setInt(2, anno);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				double peso= res.getDouble("peso");
				
				if(peso>0) {
					
					Coppia c= new Coppia(idMap.get(res.getString("id2")), idMap.get(res.getString("id1")), peso);
					result.add(c);
					
					
				}else if(peso<0) {
					
					Coppia c= new Coppia(idMap.get(res.getString("id1")), idMap.get(res.getString("id2")), Math.abs(peso));
					result.add(c);
					
				}

			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		
		
	}
		
	
}
