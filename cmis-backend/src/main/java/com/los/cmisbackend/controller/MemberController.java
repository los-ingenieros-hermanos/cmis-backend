package com.los.cmisbackend.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.dao.EventRepository;
import com.los.cmisbackend.dao.MemberApplicationRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.dao.UserRepository;
import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.MemberApplication;
import com.los.cmisbackend.entity.Student;
import com.los.cmisbackend.util.MemberUtil;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600, allowCredentials = "true")
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
	MemberUtil memberUtil;

	
	@GetMapping("/communities/{communityId}/members")
    public ResponseEntity<Set<Student>> getAllMembersByCommunityId(@PathVariable(value = "communityId") Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Set<Student> members = community.getMembers();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }
    
    @GetMapping("/communities/{communityId}/members/{memberId}")
    public ResponseEntity<Student> getMemberByCommunityId(@PathVariable(value = "communityId") Long communityId, @PathVariable(value = "memberId") Long memberId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Student member = community.getMembers().stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Not found Member with id = " + memberId));

        return new ResponseEntity<>(member, HttpStatus.OK);
    }

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isUserMemberOrCommunity(#communityId, authentication.principal.id)")	
	@DeleteMapping("/communities/{communityId}/members/{memberId}")
	public ResponseEntity<HttpStatus> deleteMember(@PathVariable(value = "communityId") Long communityId, @PathVariable(value = "memberId") Long memberId) {
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

		Student member = community.getMembers().stream()
				.filter(m -> m.getId().equals(memberId))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Not found Member with id = " + memberId));

		community.getMembers().remove(member);
		communityRepository.save(community);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isUserMemberOrCommunity(#communityId, authentication.principal.id)")	
	@GetMapping("/communities/{communityId}/memberApplications")
	public ResponseEntity<Set<MemberApplication>> getAllMemberApplicantsByCommunityId(@PathVariable(value = "communityId") Long communityId) {
		//if there is no community for this id return error
		communityRepository.findById(communityId
				).orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));
		
		Set<MemberApplication> memberApplications = memberApplicationRepository.findByCommunityId(communityId);

		if (memberApplications.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(memberApplications, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isUserMemberOrCommunity(#communityId, authentication.principal.id) or #studentId == authentication.principal.id")
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

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isUserMemberOrCommunity(#communityId, authentication.principal.id)")
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

	@PreAuthorize("hasRole('ADMIN') or @memberUtil.isUserMemberOrCommunity(#communityId, authentication.principal.id)")
	@PutMapping("/communities/{communityId}/memberApplications/{applicantId}/accept")
	public ResponseEntity<Student> acceptMemberToCommunity(@PathVariable(value = "communityId") Long communityId, 
													@PathVariable(value = "applicantId") Long applicantId)
	{	
		MemberApplication memberApplication = memberApplicationRepository.findByCommunityIdAndStudentId(communityId, applicantId);

		if (memberApplication == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Community community = communityRepository.findById(communityId
				).orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));
		Student student = studentRepository.findById(applicantId
				).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + applicantId));

		community.addMember(student);
		communityRepository.save(community);
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
	public ResponseEntity<Student> adminAddMember(@PathVariable(value = "communityId") Long communityId, 
											@PathVariable(value = "studentId") Long studentId)
	{
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

		Student member = studentRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + studentId));

		community.addMember(member);
		communityRepository.save(community);
		return new ResponseEntity<>(member, HttpStatus.OK);
	}
}
