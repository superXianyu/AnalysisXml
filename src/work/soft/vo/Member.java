package work.soft.vo;

public class Member {
    private String memberId;
    private User muser;

    @Override
    public String toString() {
        return "Member{" +
                "memberId='" + memberId + '\'' +
                ", mUser=" + muser +
                '}';
    }

    public User getMuser() {
        return muser;
    }

    public void setMuser(User muser) {
        this.muser = muser;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
