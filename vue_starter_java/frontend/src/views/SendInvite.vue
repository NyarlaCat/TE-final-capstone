<template>
    <div id= "send-invites container-fluid">
        <br>
        <form class = "offset-md-3 col-md-6" @submit.prevent="sendInvite">
            <br>
            <h4>Invite Others to This Event</h4>
            <!--div class = "form-group">
            <label for = "invitee-role"></label>
            <select id = "invitee-role" v-model="invitee.role" class="custom-select form-control">

                <option value="attendee">Attendee</option>
                <option value="chef">Chef</option>

            </select>
            </div-->
            <div class="form-group">
            <label for="email-area">Invitee Emails</label>
            <input v-model="invitee.email" class="form-control" id="email-area" type="email"  placeholder="attendee@example.com" />
            <div class="error" v-if="errors.email">{{errors.email}}</div>
            </div>
            <button type="submit"  class="form control btn btn-secondary" :disabled="formSending">Send Invite</button>

        </form>
    </div>
</template>


<script>

    import auth from '../auth'
export default {
    name: "send-invite",
    data() {
        return {
            errors: {},
            invitee: {
                email: '',
                role: 'attendee',
                eventId : 0,
                inviteId : 0,

            },
            invitationErrors: false,
            formSending: false,
        }
    },
    methods : {
        sendInvite() {
            this.formSending = true;
            fetch(`${process.env.VUE_APP_REMOTE_API}/api/event/${this.$route.params.eventId}/invite`, {
                method: 'POST',
                headers: {
                "Authorization": "Bearer "+ auth.getToken(),
                Accept: 'application/json',
                'Content-Type': 'application/json',
                },
        body: JSON.stringify(this.invitee),
      })
      .then((response) =>{
          if(response.ok){
              //this.$router.push({ path: `/${this.$route.params.eventId}/sendinvite`, query: {invitation: 'success'}});
              this.$router.push({ name: "eventDetails", params: { eventId: this.$route.params.eventId }, query: { rsvpSuccess: true } });
          } else {
              this.invitationErrors = true;
              return response.json();
          }
      }).then((json)=>{
          if( this.invitationErrors && json.errors ) {
              this.errors = {};
              json.errors.forEach((current)=>{
                  switch( current.field ) {
                      case "email":
                          this.errors.email = current.error;
                  }
              })
          }
        this.formSending = false;
      })
        }
    }
    
}
</script>

<style scoped>

:root {
  --gxorange: #ff7f68;
  --gxyellow: #ffdb2b;
  --gxpink: #ef2871;
  --gxgreen: rgb(217, 224, 216);
   --gxgreendark: #63bd55;
  --gxwhite: #effffb;
  --gxgreentransparent: rgb(113, 216, 97, 1)
}

.form-control {
    background-color: var(--gxwhite) !important;
}

.error {
    color: red;
    font-weight: bold;
}
</style>