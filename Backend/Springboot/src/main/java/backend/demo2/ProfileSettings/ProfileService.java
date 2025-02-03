package backend.demo2.ProfileSettings;

import backend.demo2.ProfileSettings.ProfileRepository;
import backend.demo2.Server.ServerMembership;
import backend.demo2.Server.ServerMembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ServerMembershipRepository membershipRepository;

    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public Optional<Profile> getProfile(Integer id) {
        return profileRepository.findById(id);
    }

    public Profile updateProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public void deleteProfile(Integer id) {
        profileRepository.deleteById(id);
    }

    public void addMembership(Integer profileId, ServerMembership membership) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        membership.getProfiles().add(profile); // Now this will work
        membershipRepository.save(membership);
    }

    public void removeMembership(Integer profileId, Long membershipId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        ServerMembership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        membership.getProfiles().remove(profile);
        membershipRepository.save(membership);
    }
    public Optional<Profile> getProfileByUserId(Integer userId) {
        return profileRepository.findByUserId(userId);
    }

}
