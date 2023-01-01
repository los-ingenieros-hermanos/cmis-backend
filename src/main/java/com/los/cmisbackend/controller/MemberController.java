package com.los.cmisbackend.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.dao.EventRepository;
import com.los.cmisbackend.dao.MemberApplicationRepository;
import com.los.cmisbackend.dao.MemberRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.dao.UserRepository;
import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.Member;
import com.los.cmisbackend.entity.MemberApplication;
import com.los.cmisbackend.entity.Student;
import com.los.cmisbackend.util.CmisConstants;
import com.los.cmisbackend.util.MemberUtil;

@CrossOrigin(origins = "${cmis.app.baseUrl}", maxAge = 3600, allowCredentials = "true")
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/cmis")
public class MemberController {

	@Autowired
    StudentRepository studentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    EventRepository eventRepository;

	@Autowired
	MemberApplicationRepository memberApplicationRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	MemberUtil memberUtil;

	
	@GetMapping("/communities/{communityId}/members")
    public ResponseEntity<Set<Member>> getAllMembersByCommunityId(@PathVariable(value = "communityId") Long communityId, 
		@RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
		@RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) 
{

	Pageable pageable = PageRequest.of(page, size);

	Page<Member> memberPage = memberRepository.findByCommunityId(communityId, pageable);

	Set<Member> members = memberPage.getNumberOfElements() == 0 ? Collections.emptySet()
			: memberPage.toSet();

	return new ResponseEntity<>(members, HttpStatus.OK);
    }
    
    @GetMapping("/communities/{communityId}/members/{studentId}")
    public ResponseEntity<Member> getMemberByCommunityId(@PathVariable(value = "communityId") Long communityId, 
															@PathVariable(value = "studentId") Long studentId) 
	{

		Member member = memberRepository.findByCommunityIdAndStudentId(communityId, studentId);

        return new ResponseEntity<>(member, HttpStatus.OK);
    }

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isAuthorized(#communityId, authentication.principal.id)")
	@GetMapping("/communities/{communityId}/authorizedMembers")
	public ResponseEntity<Set<Member>> getAuthorizedMembersByCommunityId(@PathVariable(value = "communityId") Long communityId) {
		
		Set<Member> authorizedMembers = new HashSet<Member>(); 
		for (Member member : memberRepository.findByCommunityId(communityId)) {
			Set<String>	authorizations = member.getAuthorizations();
			if (authorizations.contains("ALL")) {
				authorizedMembers.add(member);
			}
		}

		return new ResponseEntity<>(authorizedMembers, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isAuthorized(#communityId, authentication.principal.id)")	
	@DeleteMapping("/communities/{communityId}/members/{studentId}")
	public ResponseEntity<HttpStatus> deleteMember( @PathVariable(value = "communityId") Long communityId, 
												@PathVariable(value = "studentId") Long studentId) 
	{

		Member member = memberRepository.findByCommunityIdAndStudentId(communityId, studentId);
		memberRepository.delete(member);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isAuthorized(#communityId, authentication.principal.id)")
	@DeleteMapping("/communities/{communityId}/members")
	public ResponseEntity<HttpStatus> deleteAllMembersOfTheCommunity(@PathVariable (value = "communityId") Long communityId)
	{

		Set<Member> members = memberRepository.findByCommunityId(communityId);
		memberRepository.deleteAll(members);

		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isAuthorized(#communityId, authentication.principal.id)")
	@PutMapping("/communities/{communityId}/members/{studentId}")
	public ResponseEntity<Member> updateMember(@PathVariable(value = "communityId") Long communityId, 
												@PathVariable(value = "studentId") Long studentId,
												@RequestBody  Set<String> authorizations) 
	{
		Member member = memberRepository.findByCommunityIdAndStudentId(communityId, studentId);

		member.setAuthorizations(authorizations);
		final Member updatedMember = memberRepository.save(member);
		return ResponseEntity.ok(updatedMember);
	}

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isAuthorized(#communityId, authentication.principal.id)")	
	@GetMapping("/communities/{communityId}/memberApplications")
	public ResponseEntity<Set<MemberApplication>> getAllMemberApplicantsByCommunityId(@PathVariable(value = "communityId") Long communityId,
		@RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
		@RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) 
	{

		Pageable pageable = PageRequest.of(page, size);

		Page<MemberApplication> memberApplicationPage = memberApplicationRepository.findByCommunityId(communityId, pageable);

		Set<MemberApplication> memberApplications = memberApplicationPage.getNumberOfElements() == 0 ? Collections.emptySet()
				: memberApplicationPage.toSet();

		return new ResponseEntity<>(memberApplications, HttpStatus.OK);
	}
	{

	}

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isAuthorized(#communityId, authentication.principal.id) or #studentId == authentication.principal.id")
	@GetMapping("/communities/{communityId}/memberApplications/{studentId}")
	public ResponseEntity<MemberApplication> getMemberApplication(@PathVariable(value = "communityId") Long communityId ,
														@PathVariable(value = "studentId") Long studentId)
	{
		communityRepository.findById(communityId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

		MemberApplication memberApplication = memberApplicationRepository.findByCommunityIdAndStudentId(communityId, studentId);

		if (memberApplication == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(memberApplication, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isAuthorized(#communityId, authentication.principal.id)")
	@PutMapping("/communities/{communityId}/memberApplications/{applicantId}/reject")
	public ResponseEntity<Student> rejectMemberToCommunity(@PathVariable(value = "communityId") Long communityId, 
														@PathVariable(value = "applicantId") Long applicantId)
	{
		communityRepository.findById(communityId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

		MemberApplication memberApplication = memberApplicationRepository.findByCommunityIdAndStudentId(communityId, applicantId);
		if (memberApplication == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		memberApplicationRepository.delete(memberApplication);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isAuthorized(#communityId, authentication.principal.id)")
	@PutMapping("/communities/{communityId}/memberApplications/{studentId}/accept")
	public ResponseEntity<Student> acceptMemberToCommunity(@PathVariable(value = "communityId") Long communityId, 
													@PathVariable(value = "studentId") Long studentId,
													@RequestBody(required = false) Set<String> authorizations)
	{	
		MemberApplication memberApplication = memberApplicationRepository.findByCommunityIdAndStudentId(communityId, studentId);

		if (memberApplication == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

		Community community = communityRepository.findById(communityId
				).orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

		Member member = memberRepository.findByCommunityIdAndStudentId(communityId, studentId);
		if (member != null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (authorizations == null) {
			authorizations = new HashSet<>();
			authorizations.add("NONE");
		}

		member = new Member(student, community, authorizations);
		memberRepository.save(member);
		community.addMember(member);
		memberApplicationRepository.delete(memberApplication);
		return new ResponseEntity<>(student, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or #studentId == authentication.principal.id")
	@PostMapping("/communities/{communityId}/apply/{studentId}")
	public ResponseEntity<MemberApplication> memberApplication(@PathVariable(value = "communityId") Long communityId, 
											@PathVariable(value = "studentId") Long studentId, 
											@RequestBody String message)
	{

		MemberApplication memberApplication = memberApplicationRepository.findByCommunityIdAndStudentId(communityId, studentId);
		if (memberApplication != null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

		Student memberApplicant = studentRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + studentId));

		// create member application and add to community member applicant list
		memberApplication = new MemberApplication(memberApplicant, community, message);
		community.addMemberApplication(memberApplication);
		communityRepository.save(community);
		return new ResponseEntity<>(memberApplication, HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole('ADMIN') or #studentId == authentication.principal.id")
	@DeleteMapping("/communities/{communityId}/cancelApplication/{studentId}")
	public ResponseEntity<Student> deleteMemberApplication(@PathVariable(value = "communityId") Long communityId, 
													@PathVariable(value = "studentId") Long studentId)
	{
		MemberApplication memberApplication = memberApplicationRepository.findByCommunityIdAndStudentId(communityId, studentId);
		if (memberApplication == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		memberApplicationRepository.delete(memberApplication);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/communities/{communityId}/adminAddMember/{studentId}")
	public ResponseEntity<Member> adminAddMember(@PathVariable(value = "communityId") Long communityId, 
											@PathVariable(value = "studentId") Long studentId,
											@RequestBody(required = false) Set<String> authorizations)
	{
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + studentId));

		Member member = memberRepository.findByCommunityIdAndStudentId(communityId, studentId);
		if (member != null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}	
		member = new Member(student, community, authorizations);
		memberRepository.save(member);
		community.addMember(member);
		communityRepository.save(community);
		return new ResponseEntity<>(member, HttpStatus.OK);
	}
}
