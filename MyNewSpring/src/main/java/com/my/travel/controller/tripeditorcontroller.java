package com.my.travel.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.my.travel.components.Stateful;
import com.my.travel.components.Stateful2;
import com.my.travel.components.Tosave;
import com.my.travel.dao.CityRepository;
import com.my.travel.dao.PicRepository;
import com.my.travel.dao.SightseeingRepository;
import com.my.travel.dao.TripRepository;
import com.my.travel.dao.TripSightseeingRepository;
import com.my.travel.model2.City;
import com.my.travel.model2.Pic;
import com.my.travel.model2.Sightseeing;
import com.my.travel.model2.Trip;
import com.my.travel.model2.TripSightseeing;


@CrossOrigin
@Controller
@SessionAttributes({ "requestedTrip", "sights", "sights2", "fooOption", "requestedSight", "stateful",
		"requestedtrSight" })
/*@RequestMapping(value = "/web")
*/public class tripeditorcontroller {

	@Autowired // Which is auto-generated by Spring, we will use it to handle the data
	private TripRepository tripRepository;
	
	@Autowired // Which is auto-generated by Spring, we will use it to handle the data
	private SightseeingRepository sightseeingRepository;

	@Autowired // Which is auto-generated by Spring, we will use it to handle the data
	private CityRepository cityRepository;

	@Autowired // Which is auto-generated by Spring, we will use it to handle the data
	private TripSightseeingRepository tripSightseeingRepository;


	
	
	@Autowired // Which is auto-generated by Spring, we will use it to handle the data
	private PicRepository picRepository;
	/*
	 * @RequestMapping("/edittrip/{id}") public String developer(@PathVariable int
	 * id, Model model) { model.addAttribute("developer",
	 * tripRepository.findById(id).get()); model.addAttribute("skills",
	 * tripRepository.findAll()); return "tripeditor"; }
	 */

	@RequestMapping(value = "/edittrip")
	public String notesList(Model model) {
		if (!model.containsAttribute("requestedTrip")||!model.containsAttribute("requestedSight")) {
			Trip tr = getusers().get(0);
			model.addAttribute("requestedTrip", tr);
			List<Sightseeing> l1 = getsights(tr.getIdtrip());
			model.addAttribute("sights", l1);
			List<Sightseeing> l2 = getsights2(tr);
			model.addAttribute("sights2", getsightsdiff(getsights2(tr), l1));
			model.addAttribute("requestedSight", l2.get(0));

		}
		if (!model.containsAttribute("stateful"))
			model.addAttribute("stateful", new Stateful());

		if (!model.containsAttribute("selectedTrOption")) {

			model.addAttribute("selectedTrOption", new Trip());
			model.addAttribute("fooOption", new Sightseeing());
		}
		return "tripeditor";
	}

/*	@RequestMapping(value = "/addcityto")
	public String notesList1() {
		City a = new City();
		a.setCityName("qwqwq");
		cityRepository.save(a);
		Sightseeing sightseeing = new Sightseeing();
		sightseeing.setCity(a);
		sightseeing.setSightseeingsname("dsadsadsdsa");
		sightseeingRepository.save(sightseeing);
		
		//System.out.println("sds".substring(beginIndex, endIndex));
		System.out.println(picRepository.findAllBytripSightseeingIdtripSightseeingIn(10));
		
		System.out.println(picRepository.findAllBytripSightseeingIdtripSightseeingIn(10));

		return "tripeditor";

	}*/
	
	@PostMapping("/selecttrip")
	public String select(@RequestParam Trip nameoftrip, Model model) {

		model.addAttribute("requestedTrip", nameoftrip);

		List<Sightseeing> l1 = getsights(nameoftrip.getIdtrip());
		model.addAttribute("sights", l1);
		model.addAttribute("sights2", getsightsdiff(getsights2(nameoftrip), l1));
		return "tripeditor";
	}

	@PostMapping("/selectsight")
	public String selectsight(@RequestParam Sightseeing nameofsight, Model model,
			@ModelAttribute("requestedTrip") Trip requestedTrip,
			@ModelAttribute("sights") List<Sightseeing> sightslist) {

		// model.addAttribute("requestedSight", nameofsight);
		int ind = nameofsight.getIdsightseeings();
		boolean ans = sightslist.stream().filter(o -> o.getIdsightseeings() == ind).findFirst().isPresent();
		if (!ans) {
			TripSightseeing tripSightseeing = new TripSightseeing();
			tripSightseeing.setSightseeing(nameofsight);
			tripSightseeing.setTrip(requestedTrip);
			tripSightseeingRepository.save(tripSightseeing);
			/*
			 * Hibernate.initialize(requestedTrip.getTripSightseeings());
			 * requestedTrip.addTripSightseeing(tripSightseeing);
			 */
		}
		initSights(model, requestedTrip);

		// model.addAttribute("requestedSightgs", gson.toJson(nameofsight));

		model.addAttribute("requestedSight", nameofsight);
		model.addAttribute("requestedtrSight", tripSightseeingRepository
				.findOneByTripAndSightseeing(requestedTrip, nameofsight).getIdtripSightseeing());
		return "tripeditor";
	}
	
	
	
	
	@RequestMapping(value="/addnewsight", method = RequestMethod.POST)
	public String addnewsight(@RequestParam String nameofsight, Model model,
			@ModelAttribute("requestedTrip") Trip requestedTrip,
			@ModelAttribute("sights") List<Sightseeing> sightslist1, @ModelAttribute("sights2") List<Sightseeing> sightslist2) {

		// model.addAttribute("requestedSight", nameofsight);
	   //	int ind = nameofsight.getIdsightseeings();
		boolean ans = sightslist1.stream().filter(o -> o.getSightseeingsname() == nameofsight).findFirst().isPresent();
		boolean ans2 = sightslist2.stream().filter(o -> o.getSightseeingsname() == nameofsight).findFirst().isPresent();
		TripSightseeing tripSightseeing=new TripSightseeing();
		Sightseeing newSightsee=null;
		if (ans2&&!ans) {
			
	//		Sightseeing sightseeing = null;
			newSightsee = sightslist2.stream()
		            .filter(x -> x.getSightseeingsname() == nameofsight)
		            .findFirst()
		            .get(); 
			if(newSightsee!=null) {
			tripSightseeing.setSightseeing(newSightsee);
			tripSightseeing.setTrip(requestedTrip);
			tripSightseeing=tripSightseeingRepository.save(tripSightseeing);
			
			}

		}else if(!ans2&&!ans) {
			newSightsee=new Sightseeing();
			newSightsee.setCity(requestedTrip.getCity());
			newSightsee.setSightseeingsname(nameofsight);
			newSightsee=sightseeingRepository.save(newSightsee);
			tripSightseeing.setSightseeing(newSightsee);
			tripSightseeing.setTrip(requestedTrip);
			tripSightseeing=tripSightseeingRepository.save(tripSightseeing);	
		}else if(ans) {
			
			newSightsee = sightslist1.stream()
		            .filter(x -> x.getSightseeingsname() == nameofsight)
		            .findFirst()
		            .get(); 
			tripSightseeing=tripSightseeingRepository.findOneByTripAndSightseeing(requestedTrip, newSightsee);
		}
		
		initSights(model, requestedTrip);

		// model.addAttribute("requestedSightgs", gson.toJson(nameofsight));

		model.addAttribute("requestedSight", newSightsee);
		model.addAttribute("requestedtrSight", tripSightseeing.getIdtripSightseeing());
		return "tripeditor";
	}
	
	
	

	@ModelAttribute("trips")
	public List<Trip> getusers() {
		return tripRepository.findAll();
	}

	public List<Sightseeing> getsights(int id) {
		return sightseeingRepository.findBytripSightseeingsTripIdtripIn(id);
	}

	public List<Sightseeing> getsights2(Trip id) {
		List<Sightseeing> l2 = sightseeingRepository.findBycityIn(id.getCity());
		return l2;
	}

	public int getsights3(Trip id) {
		return (cityRepository.findByTripsIn(id)).getIdcities();
	}

	public List<Sightseeing> getsightsdiff(List<Sightseeing> l1, List<Sightseeing> l2) {
		Set<Sightseeing> ad = new HashSet<Sightseeing>(l1);
		Set<Sightseeing> bd = new HashSet<Sightseeing>(l2);
		ad.removeAll(bd);
		List<Sightseeing> mainList = new ArrayList<Sightseeing>();
		mainList.addAll(ad);
		return mainList;
	}

	public void initSights(Model model, Trip trip) {

		List<Sightseeing> l1 = getsights(trip.getIdtrip());
		model.addAttribute("sights", l1);
		model.addAttribute("sights2", getsightsdiff(getsights2(trip), l1));
	}

	/*
	 * @PostMapping("/addpic") public @ResponseBody String
	 * getSearchUserProfiles(@RequestBody Tosave search, HttpServletRequest request)
	 * { System.out.println(search.getAddr()); return "tripeditor";
	 * 
	 * // your logic next }
	 */

	@RequestMapping(method = RequestMethod.POST, value = "/addpic")
	@ResponseBody
	public String performLogin(@RequestBody Tosave json, HttpServletRequest request, HttpServletResponse response) {
		System.out.println(json);
		Stateful.imgdetails.add(json);
		return "fileone";
	}
	

	@RequestMapping("/check")     
	@ResponseBody
	public List<Tosave> check(@RequestParam Integer id, HttpServletRequest request, HttpServletResponse response, Model model) {
	//    model.addAttribute("tbldet", Stateful.imgdetails);
	    return  Stateful.imgdetails;
	}	
	
	@RequestMapping("/check1")     
	@ResponseBody
	public List<Tosave> check1(@RequestParam(value = "id") String id, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Pic> listp=picRepository.findAllBytripSightseeingIdtripSightseeingIn(Integer.parseInt(id));

		System.out.println(id);
		Stateful2 stateful2=new Stateful2();
		stateful2.init(listp,Integer.parseInt(id));
		System.out.println(Stateful2.imgdetails+"ddd  ");
	   // model.addAttribute("tbldet", Stateful2.imgdetails);
	    return  Stateful2.imgdetails;
	}	
	
	@RequestMapping("/removepic")     
	@ResponseBody
	public void removepic(@RequestParam(value = "name") String id,@RequestParam(value = "attr") String id1, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		System.out.println(id+" dffddfdfff12 "+id1);
		if(id.equals("a")) {
			
			Tosave tmp = Stateful2.imgdetails.get(Integer.parseInt(id1));
			TripSightseeing tripSightseeing= tripSightseeingRepository.findOneByidtripSightseeing(tmp.getTrs());
			Pic pic = picRepository.findOneBypicsAddrAndTripSightseeing(tmp.getAddr(), tripSightseeing);
			tripSightseeing.removePic(pic);
			tripSightseeingRepository.save(tripSightseeing);
		}
		else if(id.equals("b")) {
		Stateful.imgdetails.remove(Integer.parseInt(id1));
		}
		


	}	
	
	@RequestMapping("/clearpiclist")     
	@ResponseBody
	public String clrlstpic(@RequestParam Integer id, HttpServletRequest request, HttpServletResponse response, Model model) {
	//    model.addAttribute("tbldet", Stateful.imgdetails);
	 Stateful.clearItems();
	 System.out.println(Stateful.imgdetails+"fddfdffd");
	 return "fileone";
	}	
	
	
	
	@RequestMapping("/dbsbmt")     
	@ResponseBody
	public String dbsbmt( HttpServletRequest request, HttpServletResponse response, Model model) {
	//    model.addAttribute("tbldet", Stateful.imgdetails);
		List<Tosave> tmp=Stateful.imgdetails;
		   tmp.forEach(
		           p-> {
		             Pic a = new Pic();
		             a.setPicsAddr(p.getAddr());
		             Optional<TripSightseeing> b=tripSightseeingRepository.findById(p.getTrs());
		             if( b.isPresent()) {a.setTripSightseeing(b.get()); picRepository.save(a);
		            // b.get().addPic(a);
		             //tripSightseeingRepository.s
		             }
		            }
		    );
		   
		   System.out.println("sdsdsddssfdS");
		   Stateful.imgdetails.clear();
	/* Stateful.clearItems();
	 System.out.println(Stateful.imgdetails+"fddfdffd");*/
	 return "fileone";
	}	
}