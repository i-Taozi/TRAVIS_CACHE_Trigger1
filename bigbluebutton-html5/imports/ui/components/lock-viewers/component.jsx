import React, { Fragment, Component } from 'react';
import { defineMessages, injectIntl } from 'react-intl';
import PropTypes from 'prop-types';
import Toggle from '/imports/ui/components/switch/component';
import NoteService from '/imports/ui/components/note/service';
import Button from '/imports/ui/components/button/component';
import Styled from './styles';

const CHAT_ENABLED = Meteor.settings.public.chat.enabled;

const intlMessages = defineMessages({
  lockViewersTitle: {
    id: 'app.lock-viewers.title',
    description: 'lock-viewers title',
  },
  closeLabel: {
    id: 'app.shortcut-help.closeLabel',
    description: 'label for close button',
  },
  closeDesc: {
    id: 'app.shortcut-help.closeDesc',
    description: 'description for close button',
  },
  lockViewersDescription: {
    id: 'app.lock-viewers.description',
    description: 'description for lock viewers feature',
  },
  featuresLable: {
    id: 'app.lock-viewers.featuresLable',
    description: 'features label',
  },
  lockStatusLabel: {
    id: 'app.lock-viewers.lockStatusLabel',
    description: 'description for close button',
  },
  webcamLabel: {
    id: 'app.lock-viewers.webcamLabel',
    description: 'label for webcam toggle',
  },
  otherViewersWebcamLabel: {
    id: 'app.lock-viewers.otherViewersWebcamLabel',
    description: 'label for other viewers webcam toggle',
  },
  microphoneLable: {
    id: 'app.lock-viewers.microphoneLable',
    description: 'label for microphone toggle',
  },
  publicChatLabel: {
    id: 'app.lock-viewers.PublicChatLabel',
    description: 'label for public chat toggle',
  },
  privateChatLable: {
    id: 'app.lock-viewers.PrivateChatLable',
    description: 'label for private chat toggle',
  },
  notesLabel: {
    id: 'app.lock-viewers.notesLabel',
    description: 'label for shared notes toggle',
  },
  userListLabel: {
    id: 'app.lock-viewers.userListLabel',
    description: 'label for user list toggle',
  },
  ariaModalTitle: {
    id: 'app.lock-viewers.ariaTitle',
    description: 'aria label for modal title',
  },
  buttonApply: {
    id: 'app.lock-viewers.button.apply',
    description: 'label for apply button',
  },
  buttonCancel: {
    id: 'app.lock-viewers.button.cancel',
    description: 'label for cancel button',
  },
  lockedLabel: {
    id: 'app.lock-viewers.locked',
    description: 'locked element label',
  },
  unlockedLabel: {
    id: 'app.lock-viewers.unlocked',
    description: 'unlocked element label',
  },
});

const propTypes = {
  closeModal: PropTypes.func.isRequired,
  intl: PropTypes.shape({
    formatMessage: PropTypes.func.isRequired,
  }).isRequired,
  meeting: PropTypes.object.isRequired,
  showToggleLabel: PropTypes.bool.isRequired,
  updateLockSettings: PropTypes.func.isRequired,
  updateWebcamsOnlyForModerator: PropTypes.func.isRequired,
};

class LockViewersComponent extends Component {
  constructor(props) {
    super(props);

    const { meeting: { lockSettingsProps, usersProp } } = this.props;

    this.state = {
      lockSettingsProps,
      usersProp,
    };
  }

  toggleLockSettings(property) {
    const { lockSettingsProps } = this.state;

    lockSettingsProps[property] = !lockSettingsProps[property];

    this.setState({
      lockSettingsProps,
    });
  }

  toggleUserProps(property) {
    const { usersProp } = this.state;

    usersProp[property] = !usersProp[property];

    this.setState({
      usersProp,
    });
  }

  displayLockStatus(status) {
    const { intl } = this.props;

    return (
      <Styled.ToggleLabel>
        {status ? intl.formatMessage(intlMessages.lockedLabel)
          : intl.formatMessage(intlMessages.unlockedLabel)
        }
      </Styled.ToggleLabel>
    );
  }

  componentWillUnmount() {
    const { closeModal } = this.props;

    closeModal();
  }

  render() {
    const {
      closeModal,
      intl,
      showToggleLabel,
      updateLockSettings,
      updateWebcamsOnlyForModerator,
    } = this.props;

    const { lockSettingsProps, usersProp } = this.state;

    const invertColors = true;

    return (
      <Styled.LockViewersModal
        onRequestClose={closeModal}
        hideBorder
        shouldShowCloseButton={false}
        contentLabel={intl.formatMessage(intlMessages.ariaModalTitle)}
      >
        <Styled.Container>
          <Styled.Header>
            <Styled.Title>{intl.formatMessage(intlMessages.lockViewersTitle)}</Styled.Title>
          </Styled.Header>
          <Styled.Description>
            {`${intl.formatMessage(intlMessages.lockViewersDescription)}`}
          </Styled.Description>

          <Styled.Form>
            <Styled.SubHeader>
              <Styled.Bold>{intl.formatMessage(intlMessages.featuresLable)}</Styled.Bold>
              <Styled.Bold>{intl.formatMessage(intlMessages.lockStatusLabel)}</Styled.Bold>
            </Styled.SubHeader>
            <Styled.Row>
              <Styled.Col aria-hidden="true">
                <Styled.FormElement>
                  <Styled.Label>
                    {intl.formatMessage(intlMessages.webcamLabel)}
                  </Styled.Label>
                </Styled.FormElement>
              </Styled.Col>
              <Styled.Col>
                <Styled.FormElementRight>
                  {this.displayLockStatus(lockSettingsProps.disableCam)}
                  <Toggle
                    icons={false}
                    defaultChecked={lockSettingsProps.disableCam}
                    onChange={() => {
                      this.toggleLockSettings('disableCam');
                    }}
                    ariaLabel={intl.formatMessage(intlMessages.webcamLabel)}
                    showToggleLabel={showToggleLabel}
                    invertColors={invertColors}
                  />
                </Styled.FormElementRight>
              </Styled.Col>
            </Styled.Row>
            <Styled.Row>
              <Styled.Col aria-hidden="true">
                <Styled.FormElement>
                  <Styled.Label>
                    {intl.formatMessage(intlMessages.otherViewersWebcamLabel)}
                  </Styled.Label>
                </Styled.FormElement>
              </Styled.Col>
              <Styled.Col>
                <Styled.FormElementRight>
                  {this.displayLockStatus(usersProp.webcamsOnlyForModerator)}
                  <Toggle
                    icons={false}
                    defaultChecked={usersProp.webcamsOnlyForModerator}
                    onChange={() => {
                      this.toggleUserProps('webcamsOnlyForModerator');
                    }}
                    ariaLabel={intl.formatMessage(intlMessages.otherViewersWebcamLabel)}
                    showToggleLabel={showToggleLabel}
                    invertColors={invertColors}
                  />
                </Styled.FormElementRight>
              </Styled.Col>
            </Styled.Row>
            <Styled.Row>
              <Styled.Col aria-hidden="true">
                <Styled.FormElement>
                  <Styled.Label>
                    {intl.formatMessage(intlMessages.microphoneLable)}
                  </Styled.Label>
                </Styled.FormElement>
              </Styled.Col>
              <Styled.Col>
                <Styled.FormElementRight>
                  {this.displayLockStatus(lockSettingsProps.disableMic)}
                  <Toggle
                    icons={false}
                    defaultChecked={lockSettingsProps.disableMic}
                    onChange={() => {
                      this.toggleLockSettings('disableMic');
                    }}
                    ariaLabel={intl.formatMessage(intlMessages.microphoneLable)}
                    showToggleLabel={showToggleLabel}
                    invertColors={invertColors}
                  />
                </Styled.FormElementRight>
              </Styled.Col>
            </Styled.Row>

            {CHAT_ENABLED ? (
              <Fragment>
                <Styled.Row>
                  <Styled.Col aria-hidden="true">
                    <Styled.FormElement>
                      <Styled.Label>
                        {intl.formatMessage(intlMessages.publicChatLabel)}
                      </Styled.Label>
                    </Styled.FormElement>
                  </Styled.Col>
                  <Styled.Col>
                    <Styled.FormElementRight>
                      {this.displayLockStatus(lockSettingsProps.disablePublicChat)}
                      <Toggle
                        icons={false}
                        defaultChecked={lockSettingsProps.disablePublicChat}
                        onChange={() => {
                          this.toggleLockSettings('disablePublicChat');
                        }}
                        ariaLabel={intl.formatMessage(intlMessages.publicChatLabel)}
                        showToggleLabel={showToggleLabel}
                        invertColors={invertColors}
                      />
                    </Styled.FormElementRight>
                  </Styled.Col>
                </Styled.Row>
                <Styled.Row>
                  <Styled.Col aria-hidden="true">
                    <Styled.FormElement>
                      <Styled.Label>
                        {intl.formatMessage(intlMessages.privateChatLable)}
                      </Styled.Label>
                    </Styled.FormElement>
                  </Styled.Col>
                  <Styled.Col>
                    <Styled.FormElementRight>
                      {this.displayLockStatus(lockSettingsProps.disablePrivateChat)}
                      <Toggle
                        icons={false}
                        defaultChecked={lockSettingsProps.disablePrivateChat}
                        onChange={() => {
                          this.toggleLockSettings('disablePrivateChat');
                        }}
                        ariaLabel={intl.formatMessage(intlMessages.privateChatLable)}
                        showToggleLabel={showToggleLabel}
                        invertColors={invertColors}
                      />
                    </Styled.FormElementRight>
                  </Styled.Col>
                </Styled.Row>
              </Fragment>
            ) : null
            }
            {NoteService.isEnabled()
              ? (
                <Styled.Row>
                  <Styled.Col aria-hidden="true">
                    <Styled.FormElement>
                      <Styled.Label>
                        {intl.formatMessage(intlMessages.notesLabel)}
                      </Styled.Label>
                    </Styled.FormElement>
                  </Styled.Col>
                  <Styled.Col>
                    <Styled.FormElementRight>
                      {this.displayLockStatus(lockSettingsProps.disableNote)}
                      <Toggle
                        icons={false}
                        defaultChecked={lockSettingsProps.disableNote}
                        onChange={() => {
                          this.toggleLockSettings('disableNote');
                        }}
                        ariaLabel={intl.formatMessage(intlMessages.notesLabel)}
                        showToggleLabel={showToggleLabel}
                        invertColors={invertColors}
                      />
                    </Styled.FormElementRight>
                  </Styled.Col>
                </Styled.Row>
              )
              : null
            }
            <Styled.Row>
              <Styled.Col aria-hidden="true">
                <Styled.FormElement>
                  <Styled.Label>
                    {intl.formatMessage(intlMessages.userListLabel)}
                  </Styled.Label>
                </Styled.FormElement>
              </Styled.Col>
              <Styled.Col>
                <Styled.FormElementRight>
                  {this.displayLockStatus(lockSettingsProps.hideUserList)}
                  <Toggle
                    icons={false}
                    defaultChecked={lockSettingsProps.hideUserList}
                    onChange={() => {
                      this.toggleLockSettings('hideUserList');
                    }}
                    ariaLabel={intl.formatMessage(intlMessages.userListLabel)}
                    showToggleLabel={showToggleLabel}
                    invertColors={invertColors}
                  />
                </Styled.FormElementRight>
              </Styled.Col>
            </Styled.Row>
          </Styled.Form>
        </Styled.Container>
        <Styled.Footer>
          <Styled.Actions>
            <Button
              label={intl.formatMessage(intlMessages.buttonCancel)}
              onClick={closeModal}
            />
            <Button
              color="primary"
              label={intl.formatMessage(intlMessages.buttonApply)}
              onClick={() => {
                updateLockSettings(lockSettingsProps);
                updateWebcamsOnlyForModerator(usersProp.webcamsOnlyForModerator);
                closeModal();
              }}
            />
          </Styled.Actions>
        </Styled.Footer>
      </Styled.LockViewersModal>
    );
  }
}

LockViewersComponent.propTypes = propTypes;

export default injectIntl(LockViewersComponent);
